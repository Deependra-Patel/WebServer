package servers;
public class IpData {
	String ip;
	Long[] history;
	int historySize;
	int startIndex;
	int endIndex;
	int count;//connection count
	int curSize;
	IpData(int historySize, String ip){
		history = new Long[historySize];
		this.historySize = historySize;
		this.ip = ip;
		startIndex = 0;
		endIndex = 0;
		count = 0;
		curSize = 0;
	}
	void insertTime(long time){
		count ++;
		if(curSize==0){
			startIndex = 0;
			endIndex = 0;
			curSize++;
			history[0] = time;
		}
		else {
			if(curSize < historySize){
				curSize++;
			}
			startIndex = (historySize + startIndex-1)%historySize;
			history[startIndex] = time;
			if(startIndex == endIndex){
				endIndex = (historySize + endIndex-1)%historySize;			
			}
		}
	}
	long getFirst(){
		return history[startIndex];
	}
	long getLast(){
		return history[endIndex];
	}
	void reset(){
		startIndex = 0;
		endIndex = 0;
		count = 0;
		curSize = 0;
	}
	boolean isEmpty(){
		return curSize == 0;
	}
	boolean isFull(){
		return historySize == curSize;
	}
	void print(){
		System.out.println("IP -- "+ip);
		System.out.println("History");
		for(long i:history){
			System.out.print(i+" ");
		}
		System.out.println();
	}
}
