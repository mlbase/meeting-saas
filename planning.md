# Planning

Priority order — work top to bottom, don't skip ahead.

---

## 1. ActionItem domain ✓

- [x] Add `status: UserStatus` field + `changeStatus()` to `User.kt`
- [x] Fix `ActionItemStatus` enum: `CANDIDATE / PICKED_UP / IN_PROGRESS / DEPLOY_WAITING / QA / RELEASED`
- [x] `pickUp(assigneeUserId)` — own invariant only (CANDIDATE state guard)
- [x] `ActionItemDomainService.pickUp` — cross-BC user status validation
- [x] `startProgress / submitForDeploy / completeDeploy / completeQA` — assignee + state validation
- [x] `changePriority` — proposer + state validation
- [x] `confirm` — duplicate confirm guard
- [x] All `ActionItemTest` + `ActionItemDomainServiceTest` pass

## 2. Meeting domain ✓

- [x] Fix `MeetingStatus` enum: `SCHEDULED / IN_PROGRESS / SUBMITTED / BATCH_PROCESSING / COMPLETED / DROPPED`
- [x] Implement `drop / start / submit / startBatchProcessing / complete`
- [x] `addParticipant / removeParticipant / hasParticipant` — Participant as internal entity
- [x] `MeetingDomainService.createMeeting` — C-level check + duplicate meeting check
- [x] All `MeetingTest` + `MeetingDomainServiceTest` pass

## 3. Package cleanup

- [ ] Move `controller/ChatController.kt` → `presentation/controller/`
- [ ] Remove or migrate `model/MeetingModels.kt` (duplicate/legacy)
- [ ] Verify all layers follow dependency rule (presentation → application → domain ← infrastructure)

## 4. Infrastructure

- [ ] Replace `InMemoryUserRepository` with real JPA implementation
- [ ] Define DB schema (Flyway or schema.sql)
- [ ] Wire remaining repositories (ActionItem, Meeting, Ticket, EpicStory)

## 5. Application layer

- [ ] `ActionItemService` — pickup, state transitions, priority change
- [ ] `MeetingService` — create (C-level check + duplicate check), submit, drop
- [ ] Global `@ControllerAdvice` — map domain/application exceptions to HTTP responses

---

## Rules

- TDD: test skeleton before implementation
- Domain models throw domain exceptions only (`InvalidStateTransitionException`, `UnauthorizedActionException`)
- Services throw application exceptions (`ResourceNotFoundException`, `AlreadyExistsException`)
- Cross-BC references by ID only — no object references