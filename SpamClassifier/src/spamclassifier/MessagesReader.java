package spamclassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;



public class MessagesReader {
    
/// General funnctionality - Reading in messages

    // Listings of the two subdirectories (regular/ and spam/)
    private File[] listing_regular = new File[0];
    private File[] listing_spam = new File[0];
    
    
    // List the regular and spam messages
    private void listDirs(File dir_location)
    {
        // List all files in the directory passed
        File[] dir_listing = dir_location.listFiles();

        // Check that there are 2 subdirectories
        if ( dir_listing.length != 2 )
        {
            System.out.println( "- Error: specified directory does not contain two subdirectories.\n" );
            Runtime.getRuntime().exit(0);
        }

        listing_regular = dir_listing[0].listFiles();
        listing_spam    = dir_listing[1].listFiles();
    }
    
    
    private void getMessages(String path, boolean forTraining) throws IOException{
        
        // Location of threadMessagese directory (the path) taken from the cmd line (first arg)
        File dir_location = new File( path );
        
        // Check if the cmd line arg is a directory
        if ( !dir_location.isDirectory() )
        {
            System.out.println( "- Error: cmd line arg not a directory.\n" );
            Runtime.getRuntime().exit(0);
        }

        // Initialize the regular and spam lists
        listDirs(dir_location);

        if (forTraining){
            // Read the e-mail messages
            readTrainingMessages(Bayespam.MessageType.NORMAL);
            readTrainingMessages(Bayespam.MessageType.SPAM);
        } else {
            // Read the e-mail messages
            readEvalMessages(Bayespam.MessageType.NORMAL);
            readEvalMessages(Bayespam.MessageType.SPAM);
        }

    }    
    
    
/// Functionality related to reading in training data:
    
    // A hash table for the vocabulary (word searching is very fast in a hash table)
    private Hashtable <String, Bayespam.Multiple_Counter> vocabTraining = new Hashtable <String, Bayespam.Multiple_Counter> ();
    
    private int nMessagesRegular;
    private int nMessagesSpam;
    
    
    // Add a word to the vocabulary
    private void addWordTraining(String word, Bayespam.MessageType type)
    {
        Bayespam.Multiple_Counter counter = new Bayespam.Multiple_Counter();

        if ( vocabTraining.containsKey(word) ){                 // if word exists already in the vocabulary..
            counter = vocabTraining.get(word);                  // get the counter from the hashtable
        }
        counter.incrementCounter(type);                         // increase the counter appropriately

        vocabTraining.put(word, counter);                       // put the word with its counter into the hashtable
    }
    
    
    // Read the words from messages and add them to your vocabulary. The boolean type determines whether the messages are regular or not  
    private void readTrainingMessages(Bayespam.MessageType type)
    throws IOException
    {
        TextCleaner cleaner = new TextCleaner();
        String tmp;
        File[] messages = new File[0];

        if (type == Bayespam.MessageType.NORMAL){
            messages = listing_regular;
            nMessagesRegular = messages.length; /// Save number of regular messages/mails
        } else {
            messages = listing_spam;
            nMessagesSpam = messages.length;    /// Save number of spam messages/mails
        }
        
        for (int i = 0; i < messages.length; ++i)
        {
            FileInputStream i_s = new FileInputStream( messages[i] );
            BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
            String line;
            String word;
            
            while ((line = in.readLine()) != null)                      // read a line
            {
                StringTokenizer st = new StringTokenizer(line);         // parse it into words/tokens
        
                while (st.hasMoreTokens())                  // while there are stille words left..
                {
                    tmp = cleaner.returnCleanText(st.nextToken()); /// First clean input tokens, before adding them to vocabulary
                    if (tmp != null)
                        addWordTraining(tmp, type);                  // add word to the vocabulary
                }
            }

            in.close();
        }
    }
    
    
    public Hashtable<String, Bayespam.Multiple_Counter> getTrainingVocab(String path) throws IOException {
        getMessages(path, true);
        return vocabTraining;
    }

    public int getnMessagesRegular() {
        return nMessagesRegular;
    }

    public int getnMessagesSpam() {
        return nMessagesSpam;
    }
    
    
    
 /// Functionality related to reading in evaluation (test) data:
    
    private ArrayList<EvalMessage> evaluationMessages = new ArrayList<EvalMessage>();
    
    
    public void readEvalMessages(Bayespam.MessageType type) throws FileNotFoundException, IOException {
        
        
        TextCleaner cleaner = new TextCleaner();
        String tmp;
        File[] messages = new File[0];

        if (type == Bayespam.MessageType.NORMAL){
            messages = listing_regular;
            nMessagesRegular = messages.length; /// Save number of regular messages/mails
        } else {
            messages = listing_spam;
            nMessagesSpam = messages.length;    /// Save number of spam messages/mails
        }
        
        for (int i = 0; i < messages.length; ++i){
            
            /// A hash table per evaluation message containing the words containd in the eval-e-mail plus the respective word count
            Hashtable <String, Integer> vocabEval = new Hashtable <String, Integer> ();
            
            FileInputStream i_s = new FileInputStream( messages[i] );
            BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
            String line;
            String word;
            
            while ((line = in.readLine()) != null){                     // read a line
                
                StringTokenizer st = new StringTokenizer(line);         // parse it into words/tokens
        
                while (st.hasMoreTokens()){                             // while there are stille words left..
                
                    tmp = cleaner.returnCleanText(st.nextToken());      /// First clean input tokens, before adding them to vocabulary
                    if (tmp != null){
                        int counter = 1;                                /// word has occured ar least once
                        if (vocabEval.containsKey(tmp)){
                            counter = vocabEval.get(tmp);
                            counter++;
                        }
                        vocabEval.put(tmp, counter);                    /// add word to list of words in test mail
                    }
                }
            }
            
            evaluationMessages.add(new EvalMessage(vocabEval, type));   /// add messages vocab and its type to list of messages
            
            in.close();
        }
    }
    
    public ArrayList<EvalMessage> getEvaluationSet(String path) throws IOException{
        getMessages(path, false);
        return evaluationMessages;
    }
}
