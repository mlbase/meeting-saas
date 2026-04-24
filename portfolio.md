> 👉 Overview
>
- 회의 진행 중 LLM AI가 발화 내용을 분석하여 Action Item을 자동 추출
- 추출된 Action Item에 담당 후보자를 즉시 지정
- 관리자 승인 시 Sprint Epic으로 자동 전환 및 팀에 할당

> 👉 Why
>
- 스타트업 근무 경험 중, 일정 관리에 팀원들의 개인 시간이 과도하게 소모되는 것을 체감했다.
- 회의 후 회의록을 작성해도 사람이 직접 정리하다 보니 내용 해석의 차이와 누락이 발생했고, 이를 조율하는 과정에서 추가적인 시간이 낭비됐다.
- 회의 결과를 즉시 Epic Story로 전환하면 Sprint 계획 시 우선순위 기반의 효율적인 픽업이 가능하다고 판단했다. 실제로 Sprint 중반에 누락된 긴급 작업이 발생해 Sprint가 깨지는 상황을 반복적으로 경험했기 때문이다.

> 👉 Technical Challenges
>
- Whisper API 등 외부 LLM 플랫폼을 사용하므로 특정 벤더에 lock-in되는 리스크가 있다. 이를 완화하기 위해 Spring AI를 도입해 AI 플랫폼 교체 가능성을 열어두었다.
- 온디바이스 STT를 사용하지 않을 경우 LLM API 비용이 사용량에 비례해 급증할 수 있어, 비용 대비 효과를 지속적으로 검토해야 한다.
- 온디바이스 STT 도입 시에는 기기별 인식 정확도 편차 문제와 플랫폼별 개발 리소스 증가라는 별도의 trade-off가 발생한다.

> 👉 Architecture
>
```
Client (STOMP/WebSocket)
  └→ Meeting Domain (비즈니스 규칙 처리 및 상태 전이)
       └→ Batch Job 등록 (회의 제출 시)
            └→ STT 처리 (on-device STT or Whisper API)
                 └→ LLM 요청 (Action Item 추출)
                      └→ Epic Story / Action Item 자동 생성
                           └→ 상태 전이 (BATCH_PROCESSING → COMPLETED)
```

> 👉 Tech Decisions
>
- **STOMP over WebSocket**: 실시간 회의 텍스트 데이터 송수신에 사용. 연결 이후 처리는 Batch로 위임해 서버 부하를 분리
- **Spring AI**: AI 플랫폼(OpenAI, Anthropic 등) 전환 가능성을 고려한 추상화 레이어
- **Document 기반 NoSQL**: 회의록 데이터는 스키마가 유동적이고 조회 패턴이 단순해 RDB보다 적합하다고 판단
- **DDD + Layered Architecture**: Meeting, ActionItem 등 핵심 도메인의 비즈니스 규칙을 domain layer에 응집시켜 테스트 용이성과 유지보수성 확보

> 👉 Implementation
>
- (작성 중)

> 👉 Troubleshooting
>
**Whisper API 및 온디바이스 STT 테스트 전략 수립의 어려움**
>
- Whisper API는 외부 네트워크 호출이 필요하기 때문에 단위 테스트에서 직접 호출하면 비용 발생, 응답 지연, 결과 비결정성이라는 세 가지 문제가 동시에 발생했다. 반면 단순히 Mock으로 대체하면 실제 API 응답 형식의 변화나 오류 케이스를 검증할 수 없었다.
- 온디바이스 STT는 기기 환경 자체가 테스트 인프라에 존재하지 않기 때문에 CI 환경에서 재현이 불가능했고, 기기별 인식 정확도 편차를 어떤 기준으로 검증할지 정의하기도 어려웠다.
>
**해결 전략**
>
- STT 처리 로직을 `SttClient` 인터페이스로 추상화하고, 실제 구현체(WhisperSttClient, OnDeviceSttClient)와 테스트용 구현체(StubSttClient)를 분리했다. 이를 통해 도메인 로직 테스트에서는 Stub으로 빠르게 검증하고, 실제 API 연동 검증은 별도의 통합 테스트 레이어에서만 수행하도록 테스트 레이어를 분리했다.
- Whisper API 통합 테스트는 `@Tag("integration")`으로 분리해 CI에서는 기본적으로 제외하고, 별도 환경에서만 실행하도록 구성했다.
- 온디바이스 STT 정확도 검증은 사전에 정의한 기준 음성 샘플과 기대 텍스트를 비교하는 방식의 오프라인 벤치마크 테스트로 대체했다.

> 👉 Result
>
- (작성 중)

> 👉 Trade-off
>
- **실시간 처리 vs Batch 처리**: 실시간 스트리밍 분석 대신 Batch 처리를 선택해 구현 복잡도를 낮췄으나, 회의 종료 후 결과 확인까지 지연이 발생한다. 즉각적인 피드백이 필요한 케이스에는 적합하지 않다.
- **외부 STT API vs 온디바이스 STT**: 외부 API는 정확도는 높지만 비용과 벤더 의존성 문제가 있고, 온디바이스는 비용 절감이 가능하나 정확도 편차와 개발 비용이 증가한다.

> 👉 Retrospective
>
- (작성 중)
