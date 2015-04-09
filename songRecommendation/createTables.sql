drop table if exists DifficlutyOrdering;
create table DifficultyOrdering(
	sourceSongID INT,
	destinationSongID INT,
	support double,
	confidence double,
	primary key (sourceSongID, destinationSongID)
);

drop table if exists UserSongTestData;
create table UserSongTestData(
	songID INT,
	userID INT,
	primary key (songID, userID)
);

drop table if exists UserSongTrainData;
create table UserSongTrainData(
	songID INT,
	userID INT,
	primary key (songID, userID)
);

drop table if exists Songs;
create table Songs(
	songID INT,
	primary key (songID)
);

drop table if exists Users;
create table Users(
	userID INT,
	primary key (userID)
);
