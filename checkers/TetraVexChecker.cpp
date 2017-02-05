#include<bits/stdc++.h>
#include <windows.h>
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
string ExePath() {
    char buffer[MAX_PATH];
    GetModuleFileName( NULL, buffer, MAX_PATH );
    string::size_type pos = string( buffer ).find_last_of( "\\/" );
    return string( buffer ).substr( 0, pos);
}
Piece P[10][10];
char ans[30][30];
string inputFilename(int k){
	stringstream ss; 
	ss<<k;
	string r;
	ss>>r;
	return ExePath()+"\\judge"+r+".in";
}
int N; 
void print(){
	for(int i = 0; i<3*N; i++){
		for(int j = 0; j<3*N; j++){
			cout<<ans[i][j];
		}
		cout<<endl;
	}
}

int main(int argc, char *argv[]){
	int tcn = atoi(argv[1]);
	ifstream fin;
	fin.open(inputFilename(tcn).c_str());
	if(!fin.good()){
		cout<<"O"<<endl;
		cout<<"file bad:"<<fin.bad()<<endl;
		cout<<"file eof:"<<fin.eof()<<endl;
		cout<<"file fail:"<<fin.fail()<<endl;
		cout<<"file good:"<<fin.good()<<endl;
		return 0;
	}
	fin>>N;
	for(int i = 0; i<3*N; i++){
		string s; getline(cin,s);
		for(int j = 0; j<3*N; j++){
			ans[i][j] = s[j];
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
		print();
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
		print();
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
		print();
		return 0;
	}
	cout<<"AC"<<endl;
	cout<<"All checks passed; accepted."<<endl;
	cout<<"N="<<N<<endl;
	print();
	return 0;
}