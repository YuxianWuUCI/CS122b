create table movies(
	id varchar(10) not null, 
	title varchar(100) not null,
	year integer not null,
	director varchar(100) not null,
	PRIMARY KEY ( id )
 )DEFAULT CHARSET=utf8;