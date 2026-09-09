-- Create the database
CREATE DATABASE IF NOT EXISTS AIAnswerEngine;

-- Use the database
USE AIAnswerEngine;

-- Create a table for users
CREATE TABLE User (
    pk_userId INT PRIMARY KEY,
    userName VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

-- Create a table for questions
CREATE TABLE Question (
    pk_questionId INT PRIMARY KEY,
    fk_userId_asks INT,
    questionText TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (fk_userId_asks) REFERENCES User(pk_userId)
);

-- Create a table for AI models
CREATE TABLE AiModel (
    pk_modelId INT PRIMARY KEY,
    modelName VARCHAR(255) NOT NULL,
    description TEXT,
    version VARCHAR(50),
    releaseDate DATE
);


-- Create a table for answers
CREATE TABLE Answer (
    pk_answerId INT PRIMARY KEY,
    fk_questionId_belongs INT,
    fk_userId_deals INT,
    answerText TEXT NOT NULL,
    fk_modelId_feeds INT,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (fk_questionId_belongs) REFERENCES Question(pk_questionId),
    FOREIGN KEY (fk_userId_deals) REFERENCES User(pk_userId),
    FOREIGN KEY (fk_modelid_feeds) REFERENCES AiModel(pk_modelId)
);

-- Create a table for user votes on answers
CREATE TABLE Vote (
    pk_voteId INT PRIMARY KEY,
    fk_answerId_corresponds INT,
    fk_userId_does INT,
    voteType ENUM('Upvote', 'Downvote') NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (fk_answerId_corresponds) REFERENCES Answer(pk_answerId),
    FOREIGN KEY (fk_userId_does) REFERENCES User(pk_userId)
);

-- Create a table for user comments on questions and answers
CREATE TABLE Comment (
    pk_commentId INT PRIMARY KEY,
    fk_questionId_relates INT,
    fk_answerId_consists INT,
    fk_userId_writes INT,
    commentText TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (fk_questionId_relates) REFERENCES Question(pk_questionId),
    FOREIGN KEY (fk_answerId_consists) REFERENCES Answer(pk_answerId),
    FOREIGN KEY (fk_userId_writes) REFERENCES User(pk_userId)
);

-- Create a table for tags/categories
CREATE TABLE Tag (
    pk_tagId INT PRIMARY KEY,
    tagName VARCHAR(50) NOT NULL
);

-- Create a table to associate tags with questions
CREATE TABLE QuestionTag (
    fk_questionId_extends INT,
    fk_tagId_maps INT,
    PRIMARY KEY (fk_questionId_extends, fk_tagId_maps),
    FOREIGN KEY (fk_questionId_extends) REFERENCES Question(pk_questionId),
    FOREIGN KEY (fk_tagId_maps) REFERENCES Tag(pk_tagId)
);


-- Insert sample data into Users table
INSERT INTO User (pk_userId, userName, email)
VALUES
    (1, 'JohnDoe', 'john.doe@example.com'),
    (2, 'JaneSmith', 'jane.smith@example.com'),
    (3, 'BobJohnson', 'bob.johnson@example.com'),
    (4, 'AliceJohnson', 'alice.johnson@example.com'),
    (5, 'CharlieBrown', 'charlie.brown@example.com'),
    (6, 'EvaMiller', 'eva.miller@example.com');

-- Insert sample data into Questions table
INSERT INTO Question (pk_questionId, fk_userId_asks, questionText, timestamp)
VALUES
    (1, 1, 'What is the capital of France?', '2024-01-23 12:00:00'),
    (2, 2, 'How does photosynthesis work?', '2024-01-23 12:30:00'),
    (3, 3, 'What are the benefits of regular exercise?', '2024-01-23 13:00:00'),
    (4, 4, 'What is the largest mammal on Earth?', '2024-01-24 09:45:00'),
    (5, 5, 'How does the Internet work?', '2024-01-24 10:30:00'),
    (6, 6, 'What are the key principles of effective time management?', '2024-01-24 11:15:00');
    
-- Insert sample data into AIModels table
INSERT INTO AiModel (pk_modelId, modelName, description, version, releaseDate)
VALUES
    (1, 'SmartBot v1.0', 'An advanced AI model for answering a wide range of questions.', '1.0', '2024-01-01'),
    (2, 'SmartBot v2.0', 'Enhanced AI model with improved accuracy and faster response times.', '2.0', '2024-02-01');

-- Insert sample data into Answers table
INSERT INTO Answer (pk_answerId, fk_questionId_belongs, fk_userId_deals, answerText, timestamp, fk_modelId_feeds)
VALUES
    (1, 1, 2, 'The capital of France is Paris.', '2024-01-23 12:15:00', 1),
    (2, 2, 1, 'Photosynthesis is the process by which plants...', '2024-01-23 13:00:00', 1),
    (3, 3, 2, 'Regular exercise has numerous benefits, including...', '2024-01-23 13:30:00', 1),
    (4, 4, 5, 'The largest mammal on Earth is the blue whale.', '2024-01-24 10:00:00', 1),
    (5, 5, 6, 'The Internet works through a global network of computers...', '2024-01-24 11:00:00', 1),
    (6, 6, 4, 'Effective time management involves setting goals, prioritizing tasks...', '2024-01-24 11:45:00', 1);

-- Insert sample data into Votes table
INSERT INTO Vote (pk_voteId, fk_answerId_corresponds, fk_userId_does, voteType, timestamp)
VALUES
    (1, 1, 3, 'Upvote', '2024-01-23 12:20:00'),
    (2, 2, 3, 'Upvote', '2024-01-23 13:10:00'),
    (3, 3, 1, 'Upvote', '2024-01-23 14:00:00'),
    (4, 4, 1, 'Upvote', '2024-01-24 10:15:00'),
    (5, 5, 2, 'Upvote', '2024-01-24 11:10:00'),
    (6, 6, 3, 'Upvote', '2024-01-24 12:00:00');

-- Insert sample data into Comments table
INSERT INTO Comment (pk_commentId, fk_questionId_relates, fk_answerId_consists, fk_userId_writes, commentText, timestamp)
VALUES
    (1, 1, 1, 3, 'Great answer!', '2024-01-23 12:25:00'),
    (2, 2, 2, 1, 'Could you provide more details?', '2024-01-23 13:15:00'),
    (3, 3, 3, 2, 'Exercise is crucial for maintaining a healthy lifestyle.', '2024-01-23 14:10:00'),
    (4, 4, 4, 4, 'Thanks for the information!', '2024-01-24 10:30:00'),
    (5, 5, 5, 1, 'Great explanation!', '2024-01-24 11:15:00'),
    (6, 6, 6, 6, 'Time management is crucial in today''s fast-paced world.', '2024-01-24 12:30:00');

-- Insert sample data into Tags table
INSERT INTO Tag (pk_tagId, tagName)
VALUES
    (1, 'Geography'),
    (2, 'Biology'),
    (3, 'Health'),
    (4, 'Marine Life'),
    (5, 'Technology'),
    (6, 'Productivity');

-- Insert sample data into QuestionTags table
INSERT INTO QuestionTag (fk_questionId_extends, fk_tagId_maps)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (5, 3),
    (6, 6);

