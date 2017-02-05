#include<bits/stdc++.h>
using namespace std;

struct Piece{
	char A[4];
	int rotation;
	Piece(){}
	Piece(string s){
		for(int i = 0; i<4; i++)
			A[i] = s[i];
		rotation = 2;
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

int N;
Piece P[10][10];
char ans[100][100];
void print(){
	for(int i = 0; i<N; i++){
		for(int j = 0; j<N; j++){
			ans[3*i][3*j] = '\\';
			ans[3*i+2][3*j] = '/';
			ans[3*i][3*j+2] = '/';
			ans[3*i+2][3*j+2] = '\\';
			
			ans[3*i+1][3*j+1] = ' ';
			
			ans[3*i][3*j+1] = P[i][j].getNorth();
			ans[3*i+1][3*j+2] = P[i][j].getEast();
			ans[3*i+2][3*j+1] = P[i][j].getSouth();
			ans[3*i+1][3*j] = P[i][j].getWest();
		}
	}
	for(int i = 0; i<3*N; i++){
		for(int j = 0; j<3*N; j++){
			cout<<ans[i][j];
		}
		cout<<endl;
	}
}
bool done = false;
void backtrack(int i, int j){
	if(done) return;
	for(int k = 0; k<4; k++){
		bool valid = true;
		if(i>0 && P[i][j].getNorth()!=P[i-1][j].getSouth()) valid = false;
		if(j>0 && P[i][j].getWest()!=P[i][j-1].getEast()) valid = false;
		if(valid){
			if(i==N-1 && j==N-1){
				done = true;
			}else if(j==N-1){
				backtrack(i+1,0);
			}else backtrack(i,j+1);
		}
		if(done) return;
		P[i][j].rotate();
	}
}
int main(){
	cin>>N;
	for(int i = 0; i<N; i++){
		for(int j = 0; j<N; j++){
			string s;
			cin>>s;
			P[i][j] = Piece(s);
		}
	}
	backtrack(0,0);
	print();
}