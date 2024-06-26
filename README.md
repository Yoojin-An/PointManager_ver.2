### 요구사항
* 서비스
    * 포인트 충전, 사용, 조회
        * 잔고가 부족할 경우 포인트 사용 실패
* 동시성 처리
    * 동시에 여러 건의 포인트 충전 및 이용 요청이 들어올 경우 순차적 처리 필요</br></br>
###
###
### API
* GET '/point/{id}': 포인트 조회
* GET '/point/{id}/histories': 포인트 내역 조회
* PATCH '/point/{id}/charge': 포인트 충전
* PATCH '/point/{id}/use': 포인트 사용</br></br>
###
###
### 개발 환경
* Arcitetecture
  * Layered Architecture + Clean Architecture
    * Layered Architecture는 상위 계층이 하위 계층을 의존하는 단방향 흐름을 유지하기 때문에 강결합이 일어나고 SOLID 원칙의 개방 폐쇄 원칙을 위배하게 됨
    * Clean Architecture는 데이터 계층 및 프레젠테이션 계층이 비즈니스 로직을 의존함으로써 비즈니스 로직에 집중하게 됨 
    * 따라서 Layered Architecture에 Clean Architecture 개념을 추가하여 객체 지향적 프로그래밍 및 테스트 용이성을 강화 
    * Clean Architecture를 Layered Architecture와 결합하여 사용함으로써 상위 계층과 하위 계층 간의 의존성을 완화하고, 객체 지향적인 프로그래밍을 장려하며 테스트 용이성을 향상시킬 수 있습니다. Clean Architecture는 비즈니스 로직을 독립적인 도메인 모듈로 분리하여 계층 간의 강한 결합을 방지하고, 각 계층이 변경에 유연하게 대응할 수 있는 구조를 제공
* DB 및 ORM 설정
  * MySQL 사용
  * JPA를 통해 ORM 구현
* Test
  * 개발 환경과의 격리를 위해 H2를 테스트용 DB로 설정
  * 테스트 코드 작성 시 JUnit과 AssertJ 사용

###
###
### 패키지 구조
* controller: 유즈 케이스와 외부 클라이언트간의 인터페이스
  * PointsController: Point 충전, 사용, 조회 HTTP 요청 처리
    * DIP(의존성 역전 원칙) 적용: PointsController는 서비스 구현체가 아닌 서비스 인터페이스에 의존
* application: 비즈니스 로직을 처리하는 유즈 케이스 관리 패키지
  * PointsService: 포인트 서비스 기능에 대한 명세를 정의하며, OCP(개방 폐쇄 원칙)를 적용하여 서비스 구현을 유연하게 함
  * OptimisticLockPointsFacade: OptimisticLockPointsService에 대한 실패 처리 로직, PointsService의 구현체
  * OptimisticLockPointsService: 낙관적 락으로 구현한 비즈니스 로직
  * PessimisticLockPointService: 비관적 락으로 구현한 비즈니스 로직, PointsService의 구현체
* domain: 도메인 엔티티 관리 패키지
  * PointService: 동시성 제어를 포함한 포인트 관련 비즈니스 로직 처리
  * PointRepository: PointTable에 대한 인터페이스
  * PointHistoryRepository: PointHistoryTable에 대한 인터페이스
  * Points: 유저별 id, 현재 포인트, 업데이트 시간을 표현하는 데이터 클래스
  * PointsHistory: 유저별 내역 id, 유저 id, 충전/사용 타입, 잔고, 업데이트 시간을 표현하는 데이터 클래스
* repository: JpaRepository를 상속받은 인터페이스로, DB에 대한 CRUD 작업 정의
  * PointsRepository: Points DB 엔티티 인프라 제공</br>
  * PointsHistoryRepository: PointsHistory DB 엔티티에 대한 인프라 제공</br></br>
* exception
  * 비즈니스 로직에서 발생한 예외를 처리하기 위한 클래스
###
###
### 동시성 제어
* 낙관적 락과 비관적 락을 사용한 각각의 서비스 구현
  * 낙관적 락
    * 충돌이 거의 없을 것이라는 낙관적인 가정 하에 버전으로 정합성을 관리하는 방법. 조회 시에는 락을 걸지 않고 업데이트할 때에만 이전 버전과 현재 버전을 비교해 충돌 여부를 판단
  * 비관적 락
    * 충돌이 무조건 발생할 것이라는 비관적인 가정 하에 배타 락(Exclusive Lock)을 걸어서 다른 트랜잭션에서 락이 해제되기 전까지는 데이터를 가져갈 수 없도록 하는 방법. 데드락이 걸릴 수 있기 때문에 주의해야 함 
###    
###
### 테스트 시나리오
* 단위 테스트
  * domain
  * repository
    * PointsRepositoryTest
    * PointsHistoryRepositoryTest
  * application
    * PointsServiceTest
        * 특정 유저의 포인트 조회
            * 성공: 존재할 수 있는 아이디 입력 시 반환된 포인트 값과 기대값의 일치 상태 검증
                * 충전/사용 이력 없는 경우
                * 충전/사용 이력 있는 경우
            * 실패: 존재할 수 없는 아이디 입력 시 IllegalArgumentException 예외 처리 행위 검증
        * 특정 유저의 포인트 충전/사용 내역 조회
            * 성공: 존재할 수 있는 아이디 입력 시 반환된 포인트 충전/사용 내역과 기대값의 일치 상태 검증
                * 충전/사용 이력 없는 경우
                * 충전/사용 이력 있는 경우
            * 실패: 존재할 수 없는 아이디 입력 시 IllegalArgumentException 예외 처리 행위 검증
        * 특정 유저의 포인트 충전
            * 성공: 존재할 수 있는 아이디 및 양수 포인트 값 입력 시 포인트 증가 상태 검증
            * 실패1: 존재할 수 없는 아이디 입력 시 InterruptException 예외 처리 행위 검증
            * 실패2: 0 이하의 포인트 입력 시
                * InterruptException 예외 처리 행위 검증
                * 음수 포인트 값 입력 전 후의 일치 상태 검증
        * 특정 유저의 포인트 사용
            * 성공: 존재할 수 있는 아이디 및 잔고 이하의 양수 포인트 입력 시 포인트 감소 상태 검증
            * 실패1: 존재할 수 없는 아이디 입력 시 InterruptException 예외 처리 행위 검증
            * 실패2: 잔고 이상의 포인트 입력 시
                * InterruptException 예외 처리 행위 검증
                * 잔고 이상의 포인트 값 입력 전 후의 일치 상태 검증
            * 실패3: 음수 포인트 입력 시
                * InterruptException 예외 처리 행위 검증
                * 음수 포인트 값 입력 전 후의 일치 상태 검증
    * OptimisticLockPointsFacadeTest
      * 동시에 100명의 유저가 포인트 충전 시 성공
      * 동시에 100명의 유저가 포인트 사용 시 성공
    * PessimisticLockPointsServiceTest
      * 동시에 100명의 유저가 포인트 충전 시 성공
      * 동시에 100명의 유저가 포인트 사용 시 성공
* 통합 테스트
    * 동시에 100명의 유저에 대한 포인트 충전, 사용 요청 발생 시 포인트 증감 상태 검증
    * 동시에 다수의 유저에 대한 포인트 충전, 사용, 내역 조회 요청 발생 시 포인트 상태 검증</br></br>
* E2E 테스트
  * Controller 테스트
###
###
### 회고
* 비관적 락을 구현한 PessimisticLockPointService 이용 시 데드락 발생
  * 
* 롬복에 대한 의존성으로 compileOnly 'org.projectlombok:lombok'를 추가했음에도 @Getter, @RequiredArgsConstructor 등을 사용할 수 없는 문제
  * annotationProcessor 'org.projectlombok:lombok' 의존성을 추가하여 해결
* 동시성 제어가 안 되는 문제
  * JpaRepository의 save메소드가 upsert가 아닌 insert구문으로 동작하므로 PK에 대한 duplicate key error가 발생
* 서비스 메소드로 findPoints, findPointsHistory, chargePoints, usePoints가 있는데 chargePoints, usePoints에만 트랜잭션을 걸었음
* test 패키지에 정의한 application.yml을 읽지 못 하는 문제
