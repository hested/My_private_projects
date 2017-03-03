import java.util.*;
import edu.princeton.cs.algs4.*;

/*
   50 - number of states
   435 - number of seats
   Alabama - state
   4062608 - population
*/

public class Congress {
    private static PriorityQueue<State> mKeyPriorityQueue, mTempPriorityQueue;
    private static int mNumberOfStates, mCongressSize, mSeatsLeft;
    private static boolean mSwitcher;
    private static State mTempState;

    public Congress() {}

    private static class State implements Comparable<State> {
        private String stateName;
        private static double key; // Key calculated by the huntingtonHill method
        private int seats; // seats appointed in Congress to the state
        private int population;

        private State() {
            seats = 0;
            setKey();
        }

        private int getPopulation(){
            return population;
        }
        private double getKey(){
            return key;
        }
        private void setKey() {
            key = huntingtonHill(population, seats);
        }
        private int getSeats(){
            return seats;
        }
        private void setSeats() {
            seats++;
            mSeatsLeft--;
        }

        private String getStateName(){
            return stateName;
        }

        @Override
        public int compareTo(final State state) {
            return Double.compare(this.key, state.key);
        }
    }

    private static double huntingtonHill(int population, int seats){
        return Math.sqrt(population / (seats * (seats + 1)));
    }

    private static void initCongress() {
        while(StdIn.hasNextLine()) {
            State stateObject = new State();

            if(!mSwitcher && mSeatsLeft > (mSeatsLeft - mCongressSize)) {// This switchers is in order to ensure that we only mess with every second line for stateName
                stateObject.stateName = StdIn.readLine();
                mSwitcher = true;
            }

            if(mSwitcher && mSeatsLeft > (mSeatsLeft - mCongressSize)) { // This switchers is in order to ensure that we only mess with every second line for population
                stateObject.population = Integer.parseInt(StdIn.readLine());
                mKeyPriorityQueue.add(stateObject);
                mSwitcher = false;
            }
        }
    }

    private static void divedSeats() {
        initCongress(); // This methods diveds the first round of seats
        // WHILE LOOP FOR POLLING OF PRIORITYQUEUE
        System.out.println("Divide the " + mSeatsLeft + " remaining seats");
        // for loop for all the Temporery logic above. I intent
        while(mSeatsLeft >0 && !mKeyPriorityQueue.isEmpty()) {
            //Sets mTempState State object equal to priotyqueue max object, and deletes the max obect from the queue, i.e. poll()
            mTempState = mKeyPriorityQueue.poll();
            //Re-insert the state with the new key and increased population
            mTempState.setSeats();
            //Calculate new key for the state
            mTempState.setKey();
            // Print the state
            mTempPriorityQueue.add(mTempState); //adding the mStateObject into the priotyqueue
            //mKeyPriorityQueue.add(mTempState); //adding the mStateObject into the priotyqueue
            if(mKeyPriorityQueue.isEmpty() && !mTempPriorityQueue.isEmpty()) { // When //Sets mTempState State object equal to priotyqueue max object, and deletes the max obect from the queue, i.e. poll()
                mKeyPriorityQueue = mTempPriorityQueue;

            /*
               State mTempState = new State();
               mTempState = mTempPriorityQueue.poll();
               mKeyPriorityQueue.add(mTempState); //adding the mStateObject into the priotyqueue
             */
            }
        }
    }

    private static void writeToFile() {
        Out outOutFile = new Out("demo-out.txt");
        while(!mKeyPriorityQueue.isEmpty()) {
            mTempState = mKeyPriorityQueue.poll();
            //Print to console
            System.out.println(mTempState.getStateName() + " " + mTempState.getSeats());
            //Write to file
            outOutFile.println(mTempState.getStateName() + " " + mTempState.getSeats());
        }
    }

    public static void main(String[] args) {
        mKeyPriorityQueue = new PriorityQueue<>();
        mTempPriorityQueue = new PriorityQueue<>();
        mTempState = new State();

        mNumberOfStates = StdIn.readInt(); // READING NUMBER OF STATES
        mCongressSize = StdIn.readInt(); // READING SIZE OF CONGRESS
        mSeatsLeft = mCongressSize;
        System.out.println("Congress size is: " + mCongressSize + " - Number of states are: " + mNumberOfStates);
        System.out.println(StdIn.readLine()); // for some reason, we need to read this line, in order to get to the correct line index
        divedSeats();
        writeToFile();
    }
}
