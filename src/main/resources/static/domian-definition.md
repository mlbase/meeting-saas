# Domain Definition

---

## User

---

### properties
1. id
2. username
3. email
4. password
5. createdAt
6. updatedAt
7. isActive
8. roles(this role is not a authentication role, it is an company role)
9. companyId

### Actions
1. createUser
2. updateUser
3. deleteUser(deactivate User)
4. modifyUserRoles
5. authenticateUser


## Company

---

### properties
1. id
2. name
3. createdAt
4. updatedAt
5. isActive

### Actions
1. createCompany
2. updateCompany
3. deleteCompany(deactivate Company)
4. getCompanyUsers
5. getCompanyById
6. addUserToCompany
7. removeUserFromCompany
8. changeUserRole


## Ticket

---
### properties
1. id
2. title
3. githubRepositoryId (Optional)
4. userId (Optional)
5. companyId
6. status
7. isClosed

### Actions
1. createTicket
2. deleteTicket
3. updateTicket
4. getTicketById
5. getTicketsByUserId
6. delegateTicketToUser
7. changeTicketStatus
8. changeDelegatedUser

## Meeting

---
### properties
1. id
2. title
3. hostUserId
4. companyId
5. startTime
6. endTime

### Actions
1. createMeeting
2. updateMeeting
3. deleteMeeting(deactivate Meeting)


## ActionItem

---
### properties
1. id
2. title
3. ticketId(nullable)
4. meetingId(not null)
5. isConfirmed
6. assigneeUserId

#### Actions
1. createActionItemBulk
2. deleteActionItem
3. confirmActionItem
4. confirmActionItemBulk
5. getActionItemsByMeetingId
6. getActionItemsByTicketId
7. assignActionItemToUser

## Participant

---

### properties
1. userId
2. meetingId


### Actions
1. addParticipant
2. removeParticipant

## VoiceProfile

---

### properties
1. userId
2. voiceCharacteristics

### Actions
1. addUserToVoiceProfile
2. updateVoiceProfile
