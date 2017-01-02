package it.wsie.twitteranalyzer.model.tasks.partitioning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import it.wsie.twitteranalyzer.model.tasks.Task;
import it.wsie.twitteranalyzer.model.tasks.crawling.Node;
import it.wsie.twitteranalyzer.model.tasks.identification.Candidate;
import it.wsie.twitteranalyzer.model.tasks.identification.Mention;
import it.wsie.twitteranalyzer.model.tasks.identification.SocialBuzz;
import it.wsie.twitteranalyzer.model.tasks.identification.User;

/**
 * @author Simone Papandrea
 *
 */
public class PartitioningTask extends Task<PoliticalParticipation> {

	private static final long serialVersionUID = 8218300361518013606L;
	public static final String ID = "Partitioning";
	private static final String DESCRIPTION = "Partitioning users by candidate";
	private transient SocialBuzz mSocialBuzz;
	private transient Collection<Node> mNodes;

	public PartitioningTask() {

		super(ID, DESCRIPTION);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setArgs(Object... args) {

		Collection<Node> nodes;
		HashSet<User> users;
		Node n;

		mSocialBuzz = (SocialBuzz) args[0];
		nodes = (Collection<Node>) args[1];
		users = new HashSet<User>(mSocialBuzz.getUsers());
		setPayload(users.size());
		mNodes = new ArrayList<Node>();

		for (Node node : nodes) {

			if (!checkCache(node))
				mNodes.add(node);

			users.remove(node.getUser());
		}

		for (User user : users) {

			n = new Node(user);

			if (!checkCache(n))
				mNodes.add(n);
		}
	}

	@Override
	public void execute(PoliticalParticipation partition) throws Exception {

		partition(partition);
		setFrequencies(partition);
		completed();
	}
	
	private void partition(PoliticalParticipation partition) throws InterruptedException {

		Set<Mention> mentions;

		for (Node node : mNodes) {

			mentions = mSocialBuzz.getMentions(node.getUser());
			
			for (Mention mention : mentions)				
				for (Candidate candidate : mention.getCandidates())
					partition.add(node, mention.getMessage(), candidate);
			
			progress();
			cache(node);
			checkInterruption();
		}
	}

	private void setFrequencies(PoliticalParticipation partition) {

		int max;
		int freq;

		for (Candidate candidate : partition) {

			max = 0;

			for (Nominator nominator : partition.getNominators(candidate)) {

				freq = nominator.getMessagesCount();
				nominator.setFrequency(freq);
				max = Math.max(max, freq);
			}

			for (Nominator nominator : partition.getNominators(candidate)) {

				nominator.setNormFrequency(nominator.getMessagesCount() * 1f / max);
				nominator.setInfluence(nominator.getNode().getNormCentrality() * nominator.getNormFrequency());
			}
		}
	}

	@Override
	public PoliticalParticipation newObject() {

		return new PoliticalParticipation();
	}

}
