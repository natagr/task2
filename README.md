## Project Launch:

Before starting a project, you need to set up environment variables to manage database credentials securely. 
The application requires the following environment variables to be set:

- `USERNAME`: Your PostgreSQL username 
- `PASSWORD`: Your PostgreSQL password



## Endpoints

### Instructor Endpoints

#### 1. Create Instructor

- **URL**: `/instructor`
- **Method**: `POST`
- **Body**:
  ```json
  {
    "firstName": "instructor1",
    "lastName": "instructor1",
    "email": "instructor1@gmail.com"
  }

#### 2. Get Instructors

- **URL**: `/instructor`
- **Method**: `GET`

#### 3. Update Instructor

- **URL**: `/instructor/{id}`
- **Method**: `PUT`
- **Body**:
  ```json
  {
  "firstName": "instructor1update",
  "lastName": "instructor1update",
  "email": "instructor1update@gmail.com"
}

#### 4. Delete Instructor

- **URL**: `/instructor/{id}`
- **Method**: `DELETE`

### Course Endpoints

#### 1. Create Course

- **URL**: `/course`
- **Method**: `POST`
- **Body**:
  ```json
  {
  "name": "course1",
  "code": "CS105",
  "description": "An introductory course to programming concepts.",
  "credits": 50,
  "departments": ["COMPUTER_SCIENCE", "MATHEMATICS", "ELECTRONICS"],
  "instructorId": 1
}

#### 2. Get Course

- **URL**: `/course/{id}`
- **Method**: `GET`

#### 3. Update Course

- **URL**: `/course/{id}`
- **Method**: `PUT`
- **Body**:
  ```json
  {
  "name": "course1update",
  "code": "CS106",
  "description": "An introductory course to programming concepts UPDATE.",
  "credits": 50,
  "departments": ["COMPUTER_SCIENCE", "MATHEMATICS"],
  "instructorId": 1
}

#### 4. Delete Course

- **URL**: `/course/{id}`
- **Method**: `DELETE`

#### 5. List Courses

- **URL**: `/course/_list`
- **Method**: `POST`
- **Body**:
  ```json
  {
  "departments": ["MATHEMATICS", "ELECTRONICS"],
  "instructorId": 2,
  "page": 0,
  "size": 10
}
#### 6. Generate Courses Report

- **URL**: `/course/_report`
- **Method**: `POST`
- **Body**:
  ```json
  {
  "credits": 50,
  "departments": ["COMPUTER_SCIENCE"],
  "instructorId": 2
}

#### 7. Upload Courses

- **URL**: `/course/upload`
- **Method**: `POST`
- **File example**:
  ```[
  {
    "name": "Introduction to Programming",
    "code": "CS101",
    "description": "An introductory course to programming concepts.",
    "credits": 55,
    "departments": ["COMPUTER_SCIENCE", "MATHEMATICS", "ELECTRONICS"],
    "instructorId": 2
  },
  {
    "name": "Web Development",
    "code": "CS102",
    "description": "An introductory course to programming concepts.",
    "credits": 50,
    "departments": ["COMPUTER_SCIENCE", "MATHEMATICS"],
    "instructorId": 2
  },
  {
    "name": "Machine Learning",
    "code": "CS103",
    "description": "An introductory course to programming concepts.",
    "credits": 40,
    "departments": ["ELECTRONICS", "MATHEMATICS"],
    "instructorId": 1
  }
]

## Note: The JSON file for importing data is located at the path: src\main\resources\json\file.json
