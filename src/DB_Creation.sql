use university_db;

CREATE TABLE Account_Types (
	account_type_id INT,
    account_type_name VARCHAR(255),
    PRIMARY KEY(account_type_id)
);

INSERT INTO account_types VALUES (1, 'Student'), (2, 'Professor'), (3, 'Admin');

CREATE TABLE Accounts(
	user_id INT AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE ,
    password VARCHAR(255),
    is_password_temporary bool,
    account_type_id INT,
    PRIMARY KEY(user_id),
    FOREIGN KEY (account_type_id) REFERENCES Account_Types(account_type_id)
);

CREATE TABLE Students(
	student_id INT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    PRIMARY KEY(student_id)
);

CREATE TABLE Professors(
	professor_id INT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    PRIMARY KEY(professor_id)
);


CREATE TABLE Semesters(
    semester VARCHAR(255),
    PRIMARY KEY (semester)
);

INSERT INTO Semesters VALUES ('Fall 2024'), ('Spring 2025'), ('Fall 2025');
SELECT * FROM COURSES;
CREATE TABLE Courses(
	course_id INT AUTO_INCREMENT,
    course_name VARCHAR(255),
    course_semester VARCHAR(255),
    start_time VARCHAR(255),
    end_time VARCHAR(255),
    course_day VARCHAR(255),
    course_description VARCHAR(255),
    max_students INT,
    professor_id INT,
    PRIMARY KEY(course_id),
    FOREIGN KEY(professor_id) REFERENCES Professors(professor_id),
    FOREIGN KEY(course_semester) REFERENCES Semesters(semester)
);

CREATE TABLE Course_Requests(
	course_id INT,
    student_id INT,
    request_date DATE,
    request_time TIMESTAMP,
    request_status VARCHAR(255),
    PRIMARY KEY(course_id, student_id),
    FOREIGN KEY(course_id) REFERENCES Courses(course_id),
    FOREIGN KEY(student_id) REFERENCES Students(student_id)
);

CREATE TABLE Students_Enrolled_In_Courses(
	course_id INT,
    student_id INT,
    grade DOUBLE,
    PRIMARY KEY(course_id, student_id),
    FOREIGN KEY(course_id) REFERENCES Courses(course_id),
    FOREIGN KEY(student_id) REFERENCES Students(student_id)
);

CREATE TABLE Appointments(
	appointment_id INT,
    professor_id INT,
    student_id INT,
    appointment_date DATE,
    appointment_time TIMESTAMP,
    appointment_notes LONGTEXT,
    appointment_status VARCHAR(255),
    PRIMARY KEY(appointment_id),
    FOREIGN KEY(professor_id) REFERENCES Professors(professor_id),
    FOREIGN KEY(student_id) REFERENCES Students(student_id)
);

SELECT * FROM ACCOUNTS;