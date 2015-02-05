DROP TABLE IF EXISTS Users;
CREATE TABLE Users
(
    user_id INT PRIMARY KEY NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(102) NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    admin INT(1) NOT NULL DEFAULT 0
);

DROP TABLE IF EXISTS Messages;
CREATE TABLE Messages
(
    message_id INT PRIMARY KEY NOT NULL,
    date DATE NOT NULL,
    sender INT NOT NULL,
    receiver INT NOT NULL,
    subject TEXT NOT NULL,
    content TEXT NOT NULL,
    hidden INT(1) NOT NULL DEFAULT 0,
    read INT(1) NOT NULL DEFAULT 0,
    FOREIGN KEY(sender) REFERENCES users(user_id),
    FOREIGN KEY(receiver) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS Posts;
CREATE TABLE Posts
(
    post_id INT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    date DATE NOT NULL,
    contact TEXT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS Events;
CREATE TABLE Events 
(
  event_id INT PRIMARY KEY NOT NULL,
  post_id INT NOT NULL,
  event_date DATE NOT NULL,
  FOREIGN KEY(post_id) REFERENCES Posts(post_id)
);

DROP TABLE IF EXISTS Comments;
CREATE TABLE Comments
(
  comment_id INT PRIMARY KEY NOT NULL,
  user_id INT NOT NULL,
  content TEXT NOT NULL,
  date DATE NOT NULL,
  parent_id INT NOT NULL DEFAULT 0,
  post_id INT NOT NULL,
  FOREIGN KEY(post_id) REFERENCES Posts(post_id),
  FOREIGN KEY(user_id) REFERENCES Users(user_id)
);

DROP TABLE IF EXISTS Sessions;
CREATE TABLE Sessions
(
  session_id VARCHAR(255) NOT NULL,
  key VARCHAR(64) NOT NULL,
  value TEXT NOT NULL,
  PRIMARY KEY(session_id, key)
);

DROP TABLE IF EXISTS Ids;
CREATE TABLE Ids
(
  table_name varchar(128) PRIMARY KEY NOT NULL,
  next_id INT NOT NULL DEFAULT 0
);

DROP TABLE IF EXISTS Images;
CREATE TABLE Images
(
  image_id INT PRIMARY KEY NOT NULL,
  source TEXT NOT NULL
);

DROP TABLE IF EXISTS Post_has_Images;
CREATE TABLE Post_has_Images 
(
  post_id INT NOT NULL,
  image_id INT NOT NULL,
  PRIMARY KEY (post_id, image_id),
  FOREIGN KEY(post_id) REFERENCES Posts(post_id),
  FOREIGN KEY(image_id) REFERENCES Images(image_id)
);

INSERT INTO Ids(table_name, next_id) VALUES("Users", 1);
INSERT INTO Ids(table_name, next_id) VALUES("Messages", 1);
INSERT INTO Ids(table_name, next_id) VALUES("Posts", 1);
INSERT INTO Ids(table_name, next_id) VALUES("Events ", 1);
INSERT INTO Ids(table_name, next_id) VALUES("Comments", 1);
INSERT INTO Ids(table_name, next_id) VALUES("Images", 1);

