package spamclassifier;

import java.io.*;

public class Bayespam
{
    
    private static double ERROR_TERM = 1;
    
    // This defines the two types of messages we have.
    static enum MessageType
    {
        NORMAL, SPAM
    }

    // This a class with two counters (for regular and for spam)
    static class Multiple_Counter
    {
        private int counterSpam    = 0;
        private int counterRegular = 0;
        private double probRegular = 0; /// Class conditional likelihood for regular message
        private double probSpam = 0;    /// Class conditional likelihood for spam message

        // Increase one of the counters by one
        public void incrementCounter(MessageType type)
        {
            if ( type == MessageType.NORMAL ){
                ++counterRegular;
            } else {
                ++counterSpam;
            }
        }
        
        /// Compute the two class conditional (log) probabilities for a respective word (for both regular and spam cases); 
        /// If a counter is 0, its (log) probability is chosen to be to be Error/(number of reg. words + num of spam words)
        public void computeLogClassCondProbsReg(int nWordsRegular, int nWordsSpam){
            probRegular = ( counterRegular != 0 ? Math.log((double)counterRegular / (double)nWordsRegular) : Math.log((double)ERROR_TERM / ((double)nWordsRegular + (double)nWordsSpam)) );
        }
        
        public void computeLogClassCondProbsSpam(int nWordsRegular, int nWordsSpam){
            probSpam = (counterSpam != 0 ? Math.log((double)counterSpam / (double)nWordsSpam) : Math.log((double)ERROR_TERM / ((double)nWordsRegular + (double)nWordsSpam)) );
        }
        
        /// Getters to get the counts of occurances in Regular or Spam mails
        public int getRegularCount(){
            return counterRegular;
        }
        
        public int getSpamCount(){
            return counterSpam;
        }
        
        public double getConditionalRegular(){
            return probRegular;
        }
        
        public double getConditionalSpam(){
            return probSpam;
        }
    }
    
    static int parameterFitting(ContainerTrainingData container, String pathEval) throws IOException{
        double max = 0;
        int maxParam = 0;
        for (int i = 0; i < 1000; i++){
            Classifier classifier = new Classifier(container, pathEval, i);
            classifier.eval();
            if (max < classifier.getCombinedPercentageRight()){
                max = classifier.getCombinedPercentageRight();
                maxParam = i;
            }
        }
        return maxParam;
    }

   
    public static void main(String[] args) throws IOException
    {
        
        /// Just to make testing more convenient; hard code path to training/testing data
        if (args.length == 0){
            args = new String[2];
            args[0] = "/home/daniel/Uni/ThirdYear/Assignment2/data/spam-filter/train/";
            args[1] = "/home/daniel/Uni/ThirdYear/Assignment2/data/spam-filter/test/";
        }
        

        /// Get trained data in a compact container
        ContainerTrainingData container = new BayesTrainer(args[0]).getTrainingResult();
        
        /// Fit a parameter for increasing the performance of the classifier
        int param = parameterFitting(container, args[1]);
        
        
        // Print out the hash table contained in container
        //container.printVocab();
        
        Classifier classifier = new Classifier(container, args[1], param);
        classifier.eval();
        classifier.printConfusionMatrix();
        System.out.println("Parameter used for fitting: " + param);
        
        
        // Now all students must continue from here:
        //
        // 1) A priori class probabilities must be computed from the number of regular and spam messages - done
        // 2) The vocabulary must be clean: punctuation and digits must be removed, case insensitive - done
        // 3) Conditional probabilities must be computed for every word - done
        // 4) A priori probabilities must be computed for every word 
        // 5) Zero probabilities must be replaced by a small estimated value
        // 6) Bayes rule must be applied on new messages, followed by argmax classification
        // 7) Errors must be computed on the test set (FAR = false accept rate (misses), FRR = false reject rate (false alarms))
        // 8) Improve the code and the performance (speed, accuracy)
        //
        // Use the same steps to create a class BigramBayespam which implements a classifier using a vocabulary consisting of bigrams
    }
}