use university_db;

CREATE TABLE Account_Types (
	account_type_id INT,
    account_type_name VARCHAR(255),
    PRIMARY KEY(account_type_id)
);

CREATE TABLE Accounts(
	user_id INT,
    username VARCHAR(255),
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

CREATE TABLE Courses(
	course_id INT,
    course_name VARCHAR(255),
    course_semester VARCHAR(255),
    course_time VARCHAR(255),
    course_day VARCHAR(255),
    course_description VARCHAR(255),
    max_students INT,
    professor_id INT,
    PRIMARY KEY(course_id),
    FOREIGN KEY(professor_id) REFERENCES Professors(professor_id)
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