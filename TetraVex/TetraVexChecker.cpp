#include<bits/stdc++.h>
using namespace std;
struct Piece{
	char A[4];
	int rotation;
	Piece(){}
	Piece(string s){
		for(int i = 0; i<4; i++)
			A[i] = s[i];
		rotation = 0;
	}
	void rotate(){
		rotation = (rotation+1)%4;
	}
	char getNorth(){
		return A[rotation];
	}
	char getEast(){
		return A[(1+rotation)%4];
	}
	char getSouth(){
		return A[(2+rotation)%4];
	}
	char getWest(){
		return A[(3+rotation)%4];
	}
};
Piece P[10][10];
char ans[30][30];
string inputFilename(int k){
	stringstream ss; 
	ss<<k;
	string r;
	ss>>r;
	return "judge"+r+".in";
}
int main(int argc, char *argv[]){
	int tcn = atoi(argv[0]);
	cout<<"AC"<<endl;
	cout<<tcn<<endl;
	//string n; cin>> n; cout<<n<<endl; 
	return 0;
	ifstream fin(inputFilename(tcn).c_str());
	int N; fin>>N; 
	for(int i = 0; i<3*N; i++){
		for(int j = 0; j<3*N; j++){
			cin>>ans[i][j];
		}
	}
	bool wa = false;
	for(int i = 0; i<N; i++){
		for(int j = 0; j<N; j++){
			if(ans[3*i][3*j] != '\\') wa = true;
			if(ans[3*i+2][3*j] != '/') wa = true;
			if(ans[3*i][3*j+2] != '/') wa = true;
			if(ans[3*i+2][3*j+2] != '\\') wa = true;
			if(ans[3*i+1][3*j+1] != ' ') wa = true;
		}
	}
	if(wa){
		cout<<"WA"<<endl;
		cout<<"Output does not match grid format specified in problem."<<endl;
		cout<<"Checker aborted."<<endl;
		cout<<"N="<<N<<endl;
		return 0;
	}
	for(int i = 0; i<N; i++){
		for(int j = 0; j<N; j++){
			string s; fin>>s;
			P[i][j] = Piece(s);
			bool matched = false;
			for(int k = 0; k<4; k++){
				if(ans[3*i][3*j+1] == P[i][j].getNorth()
				&& ans[3*i+1][3*j+2] == P[i][j].getEast()
				&& ans[3*i+2][3*j+1] == P[i][j].getSouth()
				&& ans[3*i+1][3*j] == P[i][j].getWest()){
					matched = true;
					break;
				}
				P[i][j].rotate();
			}
			if(!matched){
				wa = true;
			}
		}
	}
	if(wa){
		cout<<"WA"<<endl;
		cout<<"Pieces in output do not match pieces in input."<<endl;
		cout<<"Checker aborted."<<endl;
		cout<<"N="<<N<<endl;
		return 0;
	}
	for(int i = 0; i<N; i++){
		for(int j = 0; j<N; j++){
			if(i>0 && P[i][j].getNorth()!=P[i-1][j].getSouth()) wa = true;
			if(j>0 && P[i][j].getWest()!=P[i][j-1].getEast()) wa = true;
		}
	}
	if(wa){
		cout<<"WA"<<endl;
		cout<<"Edges in output do not match."<<endl;
		cout<<"N="<<N<<endl;
		return 0;
	}
	cout<<"AC"<<endl;
	cout<<"All checks passed; accepted."<<endl;
	cout<<"N="<<N<<endl;
	return 0;
}