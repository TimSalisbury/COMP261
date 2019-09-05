public class BruteForce {


    public int search(String pattern, String text){
        char[] pArray = pattern.toCharArray();
        char[] tArray = text.toCharArray();

        int pLength = pArray.length;
        int tLength = tArray.length;

        outerLoop:
        for(int i = 0; i < tLength - pLength; i++){
            for(int j = 0; j < pLength; j++){
                if(tArray[i + j] != pArray[j]){
                    continue outerLoop; //If we find any one character that doesn't match skip this loop iteration
                }
            }
            return i;
        }

        return -1;
    }
}
