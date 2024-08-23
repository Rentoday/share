# 🅿️ 거주자 우선 주차 공간 공유 서비스 Rentoday
![image](https://github.com/user-attachments/assets/dcd3ed94-b463-4ecd-b8ed-30b51fe6bd85)

  ## 🔊 프로젝트 소개
  개인 주차 공간을 소유한 판매자들이 자신의 주차 공간을 일정 시간 판매함으로 사용자들과 주차 공간을 공유하는 서비스입니다.
  
  이 서비스를 통해 사용자들은 주차 공간을 직접 가서 확인하는 불편함을 줄입니다.
  
  Backend 2인 프로젝트로서 기능을 분리하고 책임을 공유하면서 개발을 진행했습니다.
    
  ## 👨‍💻 프로젝트 기간
  
  2024.06.25 ~ 2024.08.02까지 진행 후 리팩토링 작업을 거쳤습니다.
    
  ## 🧩 시스템 구성도
  ![스크린샷 2024-08-20 142057](https://github.com/user-attachments/assets/7b27b408-131a-4a43-87b7-6dbd031fd225)

  ## 🗝️ 주요 기능
  ![스크린샷 2024-08-19 133809](https://github.com/user-attachments/assets/3a3f1eaa-27a7-41c0-8849-6587e7e345cc)
   + **Easy search**
     + **Kakao map api**
     + **District & Time**
     + **Notifications**
     + **Notices**
    
![2](https://github.com/user-attachments/assets/de6828b7-1afc-4d1d-be2d-cff8d93a6ea6)
![스크린샷 2024-08-22 140936](https://github.com/user-attachments/assets/b90970cc-2c8c-4a4b-888a-def9e4704239)
  + **Daily Sharing**
    + **Rent it anytime 24 hours**
    + **Ask anything about parking lots**
    + **Types of Payment methods**

![스크린샷 2024-08-21 172525](https://github.com/user-attachments/assets/fe7bcdf2-7cae-4d1d-aaa7-7d75fbecd7ef)
![스크린샷 2024-08-22 132300](https://github.com/user-attachments/assets/a0582558-96a0-44af-a081-31a5847fbebe)
  + **Simple Checking**
    + **Check reservation history**
    + **Change profile**
    + **Cancel Payment**
    
  ## 🛠️ 개발 환경
    
### ✔️Front-end
     
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"> <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white"> <img src="https://img.shields.io/badge/bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white"> <img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=Axios&logoColor=white">
      
### ✔️Back-end

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> `JPA`

### ✔️Manage

<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  
  ## 🧬 데이터베이스 ERD 구조
  ![image](https://github.com/user-attachments/assets/5bd388e8-bcbe-4b28-a977-119875395db1)
서비스의 도메인에 맞추어 멤버, 주차 상품, 예약, 결제, 댓글, 알림, 공지사항 테이블을 설계하였으며, 생성 시간과 최종 수정 시간이 표시된 BaseEntity를 부모 테이블로 하여 모든 테이블의 데이터가 생성/수정 시간을 포함하도록 구성하였습니다.
    
  ## 🏳️ 화면 설계서
  화면에 출력할 데이터를 선별하기 위해 레이아웃과 위치를 고려하여 설계하였습니다.
  ![image](https://github.com/user-attachments/assets/b917a490-f080-44aa-8aa3-dd22ca64ff9d)

  ## 📜 API 명세서
  백엔드 API 서버 개발은 Java와 Spring 기반으로 진행하였으며, OpenAPI Spec에 맞는 RESTful한 API를 개발하였습니다. API 서버 개발은 Swagger와 Notion을 통해 문서화를 진행하였습니다.
  ![스크린샷 2024-08-23 090002](https://github.com/user-attachments/assets/9241172f-7a6e-4726-b373-0ac005a22737)
  ![스크린샷 2024-08-23 090013](https://github.com/user-attachments/assets/ff729a73-98b0-41a0-823b-cd0e7372cb21)

![스크린샷 2024-08-23 085623](https://github.com/user-attachments/assets/fcbf3f5e-2e3c-4db8-8d93-d5b488402501)
OpenAPI 3.0 Spec을 준수하는 Swagger를 사용하여 API 문서화를 진행하였습니다.
    
  ## 🆙 API 서버 리팩토링 (Github Commit)
![image](https://github.com/user-attachments/assets/bf0731b7-e057-4dc3-b286-5c0ffee574db)
API의 핵심적인 부분을 모두 개발한 후, 리팩토링 주기에 맞춰 지속적으로 코드를 개선하고, 로직을 최적화하였습니다.

JPQL FETCH JOIN 및 Batch를 통한 Query 최적화, 예외처리 로직 분리 등 다양한 레이어에서 최적화 및 코드 개선을 이루었습니다.

  ## 📜 형상 관리 프로세스
![image](https://github.com/user-attachments/assets/f7ef1c57-e2bf-4ead-8bd9-bba20ab24a5c)

  프로젝트의 형상 관리를 위해 Git, GitHub를 사용하였으며, 일관된 개발 프로세스를 팀 내에서 유지하기 위해 Github-Flow 모델을 도입하였습니다.
  
  이를 통해 PR 기반으로 feature → develop → main의 개발 프로세스를 수립하였습니다.

  개발을 진행하면서 쌓인 기술 부채를 해결하기 위해 그동안 각자 공부한 각종 강의/서적들을 기반으로 리팩토링을 진행할 수 있도록 2주 간의 리팩토링 기간을 설정하여 리팩토링을 진행하였습니다.

이 2주일 간의 리펙토링 기간을 통해, 백엔드에서는 AOP를 통한 객체지향적인 구조의 달성, 쿼리 최적화를 통한 DB 통신 비용의 감소 등의 향상을 이끌어낼 수 있었습니다.

이 과정을 통해 팀원들 스스로가 예전에 비해 얼마나 성장했는지 알 수 있게 된 좋은 계기가 되었습니다.
