package spamclassifier;

import java.io.IOException;
import java.util.Hashtable;

public class BayesTrainer {
    
    // A hash table for the vocabulary (word searching is very fast in a hash table)
    private Hashtable <String, Bayespam.Multiple_Counter> vocab = new Hashtable <String, Bayespam.Multiple_Counter> ();
    
    private MessagesReader reader;
    
    private int nMessagesRegular;
    private int nMessagesSpam;
    private int nMessagesTotal;
    private double p_regular;
    private double p_spam;
    private int nWordsRegular;
    private int nWordsSpam;
    private double ERROR_TERM = 1;
    
    public BayesTrainer(String path, int type) throws IOException {
        ///Get training data:
        reader = new MessagesReader(type);
        vocab = reader.getTrainingVocab(path);
    }
    
    /// Method computes the count of occurances of distinct words in regular and spam mails respectively:
    private void countRegularAndSpamWords(){
        nWordsRegular = 0;
        nWordsSpam = 0;
        for (String key : vocab.keySet()){              /// For each word in vocabulary:
            if (vocab.get(key).getRegularCount() > 0){  /// If word/token occurs in regular mail, increase count of vocabulary in regular mails
                nWordsRegular++;            
            }
            if (vocab.get(key).getSpamCount()> 0){      /// If word/token occurs in spam mail, increase count of vocabulary in spam mails
                nWordsSpam++;
            }
        }
    }
    
    /// Method that calcculates Log-Class-Conditioal-Likelihoods for all elements of vocab
    public void calculateLogClassCondLikelihoods(){
        
        for (String key : vocab.keySet()){ /// For each word, compute the two class conditional likelihoods:
            vocab.get(key).computeLogClassCondProbsReg(nWordsRegular, nWordsSpam);
            vocab.get(key).computeLogClassCondProbsSpam(nWordsRegular, nWordsSpam);
        }
        
    }
    
    private void train (){
        
        nMessagesRegular = reader.getnMessagesRegular();
        nMessagesSpam = reader.getnMessagesSpam();
        
        
        /// Compute values for for a priori class probabilities
        nMessagesTotal = nMessagesRegular + nMessagesSpam;              /// Compute total number of messages
        p_regular = Math.log((double) ((double)nMessagesRegular / (double)nMessagesTotal));        /// prior (log) probability for regular
        p_spam = Math.log((double) ((double)nMessagesSpam / (double)nMessagesTotal));              /// prior (log) probability for spam
        
        //System.out.println("nMessagesRegular: " + nMessagesRegular + " nMessagesSpam: " + nMessagesSpam + " nMessagesTotal: " + nMessagesTotal + " p_regular: " + p_regular + " p_spam: " + p_spam);
        /// Compute class conditional word probabilities:
        
        /// First, get regular- and spam-word-counts
        countRegularAndSpamWords();
        
        /// Second, compute log-class-conditional-likelihood per word
        calculateLogClassCondLikelihoods();
        
    }
    
    private ContainerTrainingData generateReturnValue(){
        ContainerTrainingData container = new ContainerTrainingData();
        container.setVocab(vocab);
        container.setnMessagesRegular(nMessagesRegular);
        container.setnMessagesSpam(nMessagesSpam);
        container.setnWordsRegular(nWordsRegular);
        container.setnWordsSpam(nWordsSpam);
        container.setP_regular(p_regular);
        container.setP_spam(p_spam);
        return container;
    }
    
    public ContainerTrainingData getTrainingResult(){
        train();
        return generateReturnValue();
    }
    
}
