/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {

	private int[] map;

	public KMP(String pattern, String text) {
		map = new int[pattern.length()];

		char[] word = pattern.toCharArray();

		int pSize = pattern.length();

		int position = 1;
		int candidate = 0;

		map[0] = -1;

		while(position < pSize){
			if(word[position] == word[candidate]){
				map[position] = map[candidate];
			}else{
				map[position] = candidate;
				candidate = map[candidate];
				while(candidate >= 0 && word[position] != word[candidate]){
					candidate = map[candidate];
				}
			}
			position++;
			candidate++;
		}
	}

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */
	public int search(String pattern, String text) {
		// TODO fill this in.
		char[] pArray = pattern.toCharArray();
		char[] tArray = text.toCharArray();

		int tSize = tArray.length;
		int pSize = pArray.length;

		int cMatch = 0;
		int index = 0;
		while(cMatch + index < tSize){
			if(pArray[index] == tArray[cMatch + index]){
				index++;
				if(index == pSize){
					return cMatch;
				}
			}else if(map[index] == -1){
				cMatch += index + 1;
				index = 0;
			}else{
				cMatch += index - map[index];
				index = map[index];
			}
		}
		
		return -1;
	}
}
