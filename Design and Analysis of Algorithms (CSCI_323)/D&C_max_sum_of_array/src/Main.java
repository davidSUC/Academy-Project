public class Main {

	public static void main(String[] args) {
		int a[] = {-2,4,6,-5,4,2,-7,8};
		int ans[] = maxSub(a,0,a.length-1); //return an array. a[0]=the maximum sum, a[1]=start position, a[2]=last position
		System.out.print("Maxium sum is " + ans[0] + ". The index range is [" + ans[1] + ", " + ans[2] + "]" );
	}
	
	private static int[] maxSub(int[] array, int s, int e) {
		if(s == e) {
			int[] ans = {array[s], s, e};
			return ans;
		}
		int mid = (s+e)/2;
		return compareSum( maxSub(array, s, mid) , maxSub(array, mid+1, e), midBest(array, s, e) );
	}	
	
	private static int[] midBest(int[] array, int s, int e) {
		if(s==e) {
			int[] ans = {array[s],s,e};
			return ans;
		};
		int mid = (s+e)/2;
		int tmp = array[mid];
		int leftN = tmp;
		int sPoint = mid-1;
		int ePoint = mid+1;
		for(int i = mid-1; i>=s; i--) {
			tmp += array[i];
			if(tmp > leftN) {
				leftN = tmp;
				sPoint = i;
			}
		}
		tmp = array[mid+1];
		int rightN = tmp;
		for(int i = mid+2; i<=e; i++) {
			tmp += array[i];
			if(tmp > rightN) {
				rightN = tmp;
				ePoint = i;
			}
		}
		int[] midSum = {leftN+rightN,sPoint,ePoint};
		return midSum;
	}

	private static int[] compareSum(int[] a, int[] b, int[] c) {
		int ans = Math.max(a[0], b[0]);
		ans = Math.max(ans, c[0]);
		if(ans == a[0]) {
			return a;
		}
		else if(ans == b[0]) {
			return b;
		}
		else return c;
	}
	
}