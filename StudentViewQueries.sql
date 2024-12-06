USE university_db;


CREATE TABLE past_courses_for_student (
    course_id INT,               
    student_id INT,              
    course_name VARCHAR(255),    
    semester VARCHAR(255),        
    PRIMARY KEY (course_id, student_id),  
    FOREIGN KEY (student_id) REFERENCES Students(student_id),
    FOREIGN KEY (course_id) REFERENCES Courses(course_id)
   
);


CREATE TABLE past_final_grades (
    grade_id INT, 
    student_id INT,                 
    course_id INT,                  
    final_grade VARCHAR(2),  
    PRIMARY KEY (grade_id),
    FOREIGN KEY (student_id) REFERENCES Students(student_id), 
    FOREIGN KEY (course_id) REFERENCES Courses(course_id) 
);


CREATE TABLE student_gpa (
    student_gpa_id INT,
    student_id INT , 
    full_name VARCHAR(255) ,           
    gpa DECIMAL(3, 2) ,                
    total_credits INT,
    PRIMARY KEY (student_gpa_id),
    FOREIGN KEY (student_id) REFERENCES Students(student_id)
);
