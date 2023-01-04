<h1>QnA</h1>
<p> <b>QnA</b>는 <b>게시판</b> 프로젝트를 <b>리팩토링</b> 하여 재 구성한 프로젝트 입니다.</p>
<p> ORM Mapper <b>MyBatis</b> -> <b>Spring Data JPA</b></p>
<p> No Test Code -> <b>With Test Code</b></p>
<p> No CI/CD -> <b>GitHub Actions</b> & <b>Docker</b> & <b>AWS EC2</b> CI/CD 구축</p>

### 📌📗 API 문서

##### REST Docs
- [QnA API Document](http://15.164.38.90/api/docs/index.html)

## 📚 BackEnd 기술 스택

| Name   | Version |
| ------ | ------- |
| <img alt="Java" src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> | 11 |
| <img alt="Spring Boot" src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> | 2.7.1 |
| <img alt="Gradle" src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> | 7.0 |
| <img  alt="H2" src="https://img.shields.io/badge/H2-09476b?style=for-the-badge&logo=Databricks&logoColor=white"> | 2.1.214 |
| <img  alt="JUnit5" src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"> | 5 |
| <img  alt="RestDocs" src="https://img.shields.io/badge/RestDocs-8CA1AF?style=for-the-badge&logo=Read the Docs&logoColor=white"> | 2.0.6 RELEASE |

### 🌈🔍️프로젝트 아키텍쳐

##### Backend
![backend](https://user-images.githubusercontent.com/53131108/208902354-e8981f02-8d7a-447d-9a24-517771db56b6.png)

##### CI/CD
![CI/CD](https://user-images.githubusercontent.com/53131108/209106762-72caa494-c869-41a0-bf65-8c1d1f633e51.png)

</br>

### 📸📦️ 데이터베이스

##### DB ERD
![image](https://user-images.githubusercontent.com/53131108/210520503-cd84f33c-a0f0-4278-94c5-01ceaf681de8.png)

</br>

## Git Flow
- default branch : main
##### Branch rule
- main - 배포 되는 브랜치 / 기능 구현 완료가 되면 PR을 보내는 default branch
- 브랜치 이름은 작업한 내용을 표현해야 합니다. 
     - ex) apply-spring-security

##### Commit Message Convention & Type
```
feat: 제목

- 내용
```

  | Type | Description |
  |------|---|
  |feat|새로운 기능 추가|
  |fix|버그 수정|
  |docs|문서 수정|
  |refactor|코드 리팩토링|
  |test|테스트 코드 작성|
  |chore|소스 코드를 건들지 않는 작업(빌드 업무 수정)|
  |conflict| git confict 해결 |
