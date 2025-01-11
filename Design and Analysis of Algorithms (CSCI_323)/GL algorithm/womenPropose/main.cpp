#include <iostream>

int main() {
	//First [] index refer to which #of men. Second [] refer to their preference, [0] is the highest preference.
	int womenPreference[4][4] = { {1,3,2,4},
			               {2,1,4,3},
    	   	  	               {1,3,2,4},
			               {4,1,3,2} };

	//First [] index refer to which #of women. Second [] refer to which men, and value 1 represent the highest preference
	////row#1=men1, colum#1=women1, value=prefernece level.
	int menPreference[4][4] = { {4,1,2,3},
			          {1,3,4,2},
			          {1,4,2,3},
			          {2,1,3,4} };
	
	//To check men and women free or not.
	//Value 0 refer free, and 1 refer engaged.
	//All defalut 0.
	bool womenStatus[4] = {};

	//Pair index number connect with which women.
	//pair[0] is the pair women1 in, and the Value of pair[0] refer to her partner.
	//All pair default values is -1 which represent pairs are not engaged.
	int pair[4] = {-1,-1,-1,-1};

	int j = 0;
	while ( !womenStatus[0] || !womenStatus[1] || !womenStatus[2] || !womenStatus[3] ){
		for (int i = 0; i < 4; i++) {
			if (womenStatus[i] == 0) {
				int menEngaging = womenPreference[i][j] - 1;
				if (pair[menEngaging] == -1) {
					pair[menEngaging] = i;
					womenStatus[i] =1;
				}
				else if(menPreference[menEngaging][pair[menEngaging]] > menPreference[menEngaging][i]){
					womenStatus[pair[menEngaging]] = 0;
					pair[menEngaging] = i;
					womenStatus[i] = 1;
				}
			}
		}
	j++;
	}
	int pairNum = 1;
	for (auto i : pair) {
		std::cout << "Pair#" << pairNum << ":(women" << pairNum << ", men" <<++ i << ")"<< std::endl;
		pairNum++;
	}
}