#include <iostream>

int main() {
	//First [] index refer to which #of men. Second [] refer to their preference, [0] is the highest preference.
	int menPreference[4][4] = { {2,3,4,1},
								{1,4,2,3},
								{1,3,4,2},
								{2,1,3,4} };

	//First [] index refer to which #of women. Second [] refer to which men, and value 1 represent the highest preference.
	//row#1=women1, colum#1=men1, value=prefernece level.
	int womenPreference[4][4] = { {1,3,2,4},
								  {2,1,4,3},
								  {1,3,2,4},
								  {4,1,3,2} };
	
	//To check men and women free or not.
	//Value 0 refer free, and 1 refer engaged.
	//All defalut 0.
	bool menStatus[4] = {};

	//Pair index number connect with which women.
	//pair[0] is the pair women1 in, and the Value of pair[0] refer to her partner.
	//All pair default values is -1 which represent pairs are not engaged.
	int pair[4] = {-1,-1,-1,-1};

	int j = 0;
	while ( !menStatus[0] || !menStatus[1] || !menStatus[2] || !menStatus[3] ){
		for (int i = 0; i < 4; i++) {
			if (menStatus[i] == 0) {
				int womenEngaging = menPreference[i][j] - 1;
				if (pair[womenEngaging] == -1) {
					pair[womenEngaging] = i;
					menStatus[i] =1;
				}
				else if(womenPreference[womenEngaging][pair[womenEngaging]] > womenPreference[womenEngaging][i]){
					menStatus[pair[womenEngaging]] = 0;
					pair[womenEngaging] = i;
					menStatus[i] = 1;
				}
			}
		}
	j++;
	}
	int pairNum = 1;
	for (auto i : pair) {
		std::cout << "Pair#" << pairNum << ":(men" << pairNum << ", women" << i << ")"<< std::endl;
		pairNum++;
	}
}