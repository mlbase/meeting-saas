# Domain Definition

---

## Bounded Contexts & Aggregate Roots

| Bounded Context | Aggregate Root | Notes |
|---|---|---|
| Identity | User | 사용자 라이프사이클, 인증 |
| Identity | Company | 회사 관리, 멤버십 |
| Meeting | Meeting | Participant를 aggregate 내부 entity로 포함 |
| Meeting | VoiceProfile | 화자 식별 데이터, userId로 참조 |
| Task | Ticket | GitHub 연동, 독립적 라이프사이클 |
| Task | ActionItem | Meeting에서 생성되지만 독립적인 조회/관리 필요 |
| Task | EpicStory | batch 처리 후 자동 생성, ActionItem들을 묶는 상위 개념 |

> **설계 원칙**
> - 다른 BC의 aggregate는 객체 직접 참조 대신 **ID 참조**
> - Participant는 독립 AR이 아닌 Meeting aggregate 내부 entity
> - 각 AR은 자체 Repository interface를 domain layer에 정의

---

## Core Feature Flows

### ① Meeting Flow
```
SCHEDULED → IN_PROGRESS → SUBMITTED → BATCH_PROCESSING → COMPLETED
           └→ DROPPED
```
- C-level 이상만 meeting 생성 가능 (role 체크)
- company 단위로 IN_PROGRESS/SUBMITTED 상태의 meeting이 존재하면 신규 생성 차단
- DROP은 SCHEDULED 상태에서만 가능
- SUBMITTED 전환 시 batch 후보로 등록
- batch 완료 시 EpicStory + ActionItem 자동 생성

### ② ActionItem Flow (batch 이후)
```
CANDIDATE → PICKED_UP → IN_PROGRESS → DEPLOY_WAITING → QA → RELEASED
```
- CANDIDATE: batch가 생성한 초기 상태
- PICKED_UP: PLANNING 상태의 user만 pickup 가능
- PICKED_UP 시 Ticket이 자동 생성되어 연결
- 입안자가 priority 변경 가능

### ③ User Status Flow
```
AVAILABLE → PLANNING → WORKING → AVAILABLE
```
- PLANNING 상태일 때 pickup 가능한 ActionItem 조회 가능
- ActionItem 조회 기준: priority DESC, createdAt ASC (오래된 것 우선)
- 조회는 페이징 처리

---

## Identity BC

---

### User (Aggregate Root)

#### Properties
1. id
2. username
3. email (VO)
4. password (VO)
5. createdAt
6. updatedAt
7. isActive
8. companyId (Company AR을 ID로 참조)
9. roles (인증 권한이 아닌 company 내 역할 — C_LEVEL, MANAGER, MEMBER 등)
10. status (UserStatus: AVAILABLE / PLANNING / WORKING)

#### Actions
1. createUser
2. updateUser
3. deleteUser (deactivate)
4. modifyUserRoles
5. authenticateUser
6. changeStatus (AVAILABLE → PLANNING → WORKING → AVAILABLE)

---

### Company (Aggregate Root)

#### Properties
1. id
2. name
3. createdAt
4. updatedAt
5. isActive

#### Actions
1. createCompany
2. updateCompany
3. deleteCompany (deactivate)
4. getCompanyUsers
5. getCompanyById
6. addUserToCompany
7. removeUserFromCompany
8. changeUserRole

---

## Meeting BC

---

### Meeting (Aggregate Root)

#### Properties
1. id
2. title
3. hostUserId (User AR을 ID로 참조, C-level 이상만 가능)
4. companyId (Company AR을 ID로 참조)
5. startTime
6. endTime
7. status (MeetingStatus: SCHEDULED / IN_PROGRESS / SUBMITTED / BATCH_PROCESSING / COMPLETED / DROPPED)
8. participants (List\<Participant\> — aggregate 내부 entity)

#### Actions
1. createMeeting (C-level 이상 role 검증 + company 단위 중복 생성 차단)
2. updateMeeting
3. dropMeeting (SCHEDULED 상태에서만 가능)
4. submitMeeting (SUBMITTED 전환 → batch 후보 등록)
5. startBatchProcessing (SUBMITTED → BATCH_PROCESSING)
6. completeMeeting (BATCH_PROCESSING → COMPLETED)
7. addParticipant
8. removeParticipant

---

### Participant (Entity — Meeting aggregate 내부)

> 독립 AR이 아님. Meeting을 통해서만 접근

#### Properties
1. userId (User AR을 ID로 참조)
2. meetingId

---

### VoiceProfile (Aggregate Root)

#### Properties
1. userId (User AR을 ID로 참조)
2. voiceCharacteristics

#### Actions
1. createVoiceProfile
2. updateVoiceProfile

---

## Task BC

---

### EpicStory (Aggregate Root)

> batch 처리 완료 후 자동 생성. ActionItem들을 묶는 상위 단위

#### Properties
1. id
2. title
3. meetingId (Meeting AR을 ID로 참조)
4. companyId (Company AR을 ID로 참조)
5. createdAt

#### Actions
1. createEpicStory (batch 처리 결과로만 생성)
2. getEpicStoryByMeetingId

---

### Ticket (Aggregate Root)

#### Properties
1. id
2. title
3. githubRepositoryId (Optional)
4. assigneeUserId (Optional, User AR을 ID로 참조)
5. companyId (Company AR을 ID로 참조)
6. status
7. isClosed

#### Actions
1. createTicket
2. deleteTicket
3. updateTicket
4. getTicketById
5. getTicketsByUserId
6. delegateTicketToUser
7. changeTicketStatus
8. changeDelegatedUser

---

### ActionItem (Aggregate Root)

#### Properties
1. id
2. title
3. meetingId (Meeting AR을 ID로 참조)
4. epicStoryId (EpicStory AR을 ID로 참조)
5. ticketId (Optional — PICKED_UP 시 자동 생성되어 연결)
6. status (ActionItemStatus: CANDIDATE / PICKED_UP / IN_PROGRESS / DEPLOY_WAITING / QA / RELEASED)
7. priority (ActionItemPriority: LOW / MEDIUM / HIGH / CRITICAL)
8. proposerUserId (입안자, User AR을 ID로 참조)
9. assigneeUserId (Optional, User AR을 ID로 참조)
10. isConfirmed
11. createdAt

#### Actions
1. createActionItemBulk (batch 처리 결과로만 생성, 초기 status = CANDIDATE)
2. deleteActionItem
3. confirmActionItem
4. confirmActionItemBulk
5. changePriority (proposerUserId 본인만 가능)
6. pickUp (assigneeUserId 지정 + status CANDIDATE → PICKED_UP + Ticket 자동 생성)
7. startProgress (PICKED_UP → IN_PROGRESS)
8. submitForDeploy (IN_PROGRESS → DEPLOY_WAITING)
9. completeDeploy (DEPLOY_WAITING → QA)
10. completeQA (QA → RELEASED)
11. getActionItemsByMeetingId
12. getActionItemsByTicketId
13. getPickupCandidates (PLANNING 상태 user용, priority DESC + createdAt ASC 페이징)
