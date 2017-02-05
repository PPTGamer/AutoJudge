#include<bits/stdc++.h>
using namespace std;

ofstream fout("output.txt");

struct Piece{
	char A[4];
	int rotation;
	Piece(){}
	Piece(char a, char b, char c, char d){
		A[0] = a;
		A[1] = b;
		A[2] = c;
		A[3] = d;
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
	string toString(){
		string s = "0000";
		s[0] = getNorth();
		s[1] = getEast();
		s[2] = getSouth();
		s[3] = getWest();
		return s;
	}
	void scramble(){
		int k = rand()%4;
		while(k--) rotate();
	}
};

char randLetter(){
	char c = rand()%26+'a';
	return c;
}

Piece A[10][10];
char rowMatches[10][10];
char colMatches[10][10];
int main(){
	srand(time(NULL));
	int N;
	cin>>N;
	cout<<N<<endl;
	for(int i = 0; i<N; i++){
		for(int j = 0; j<N; j++){
			rowMatches[i][j] = randLetter();
		}
	}
	for(int i = 0; i<N; i++){
		for(int j = 0; j<N; j++){
			colMatches[i][j] = randLetter();
		}
	}
	for(int i = 0; i<N; i++){
		for(int j = 0; j<N; j++){
			char north = '0';
			char south = '0';
			char east = '0';
			char west = '0';
			if(i>0) north = colMatches[i-1][j];
			if(i<N-1) south = colMatches[i][j];
			if(j>0) west = rowMatches[i][j-1];
			if(j<N-1) east = rowMatches[i][j];
			
			if(north=='0') north = randLetter();
			if(south=='0') south = randLetter();
			if(east=='0') east = randLetter();
			if(west=='0') west = randLetter();
			
			A[i][j] = Piece(north,east,south,west);
			A[i][j].scramble();
			if(j>0) cout<<" ";
			cout<<A[i][j].toString();
		}
		cout<<endl;
	}
}