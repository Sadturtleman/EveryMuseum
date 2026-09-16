# EveryMuseum

안드로이드 권장 아키텍쳐 샘플 코드입니다.

국립중앙박물관 [e뮤지엄 Open API](https://www.emuseum.go.kr) 의 소장품을 둘러보는 앱이고,
멀티 모듈 · MVI · Navigation 3 · Hilt 로 구성돼 있습니다.

화면 진입 시간(TTI) 계측, 프레임 드랍(jank) 계측, 비즈니스 이벤트 로깅,
환경별 피처 플래그 · AB 테스트가 앱 전체를 가로지르는 모듈로 따로 있습니다.
그 수치를 해석하는 데 필요한 기기 정보는 `:common:util` 이 한곳에서 읽어 줍니다.

## 시스템 아키텍처

한 요청은 `:app` 의 라우팅 테이블에서 시작해 화면 → UseCase → Repository 계약 → 통신 스택 순으로
한 겹씩만 내려갑니다. 각 layer 는 바로 아래 layer 의 **계약**만 알고 구현은 모릅니다.

```mermaid
flowchart LR
    app[":app<br/>Navigation 3 호스트"]
    cp[":common:presentation<br/>디자인 시스템 · MVI 베이스"]
    fp["feature:presentation<br/>home · search · detail · store"]
    fd["feature:domain<br/>UseCase · Repository 계약"]
    fdata["feature:data<br/>Repository 구현 · Mapper"]
    key["ServiceKeyInterceptor<br/>인증키를 요청마다 주입"]
    net[":common:network<br/>Retrofit · OkHttp · XML 변환"]
    ds[":common:datastore<br/>Preferences DataStore"]
    util[":common:util<br/>DeviceInfoProvider"]
    api(["e뮤지엄 Open API<br/>apis.data.go.kr"])

    app -->|라우팅 · 화면 진입| fp
    fp -->|UseCase 호출| fd
    fd -->|Repository 구현| fdata
    fdata -->|Retrofit 요청| net
    net -->|HTTPS| api

    cp -.->|MVI 베이스 · 컴포넌트| fp
    key -.->|인증키 주입| net
    fdata -.->|보관함 id 저장| ds
    util -.->|설치 ID 저장| ds

    subgraph TTI[":tti — 앱 전체를 가로지르는 계측"]
        direction LR
        tp[":tti:presentation<br/>TtiPage · 구간 Effect"]
        td[":tti:domain<br/>네 구간 정의 · 기록기"]
        tdata[":tti:data<br/>Room tti.db · SystemClock"]
        tp -->|TtiRecorder| td
        td -->|저장 · 시각 포트| tdata
    end

    subgraph LOG[":logging — 비즈니스 이벤트"]
        direction LR
        ld[":logging:domain<br/>이벤트 카탈로그 · 기록기"]
        ldata[":logging:data<br/>Hilt 조립 · 전송 구현"]
        ld -->|전송 포트| ldata
    end

    subgraph FLAG[":featureflag — 환경별 플래그 · AB"]
        direction LR
        fl[":featureflag:domain<br/>플래그 카탈로그 · 제공자"]
        fldata[":featureflag:data<br/>Hilt 조립 · 리모트 컨피그"]
        fl -->|값 출처 포트| fldata
    end

    subgraph JANK[":jank — 프레임 드랍 계측"]
        direction LR
        jp[":jank:presentation<br/>JankPage · 스크롤 감시"]
        jd[":jank:domain<br/>버킷 · 임계치 규칙"]
        jdata[":jank:data<br/>Hilt 조립 · 내보낼 곳"]
        jp -->|JankReporter| jd
        jd -->|내보낼 곳 포트| jdata
    end

    fp -->|구간 열기 · 닫기| tp
    fp -->|이벤트 기록| ld
    app -->|화면 진입 기록| ld
    fp -->|플래그 읽기| fl
    fd -->|플래그 읽기| fl
    fl -.->|배정 기록| ld
    app -->|화면 이름 등록| jp
    fp -->|스크롤 구간 알림| jp
```

> 🔍 인터랙티브 버전: [`docs/architecture.html`](docs/architecture.html)
> 모듈을 눌러 의존 관계만 추리거나 `주요 요청 경로` · `공통 레이어` · `TTI 계측` · `가로지르는 모듈`
> 네 갈래로 나눠 볼 수 있습니다.
> (GitHub 은 저장소 안의 HTML 을 렌더링하지 않습니다 — 내려받아 브라우저에서 열어 주세요)

**레이어 규칙**

| 모듈 | 성격 | 아는 것 |
|---|---|---|
| `feature:presentation` | Compose + ViewModel | 입력은 `Intent` 하나, 출력은 `UiState` 하나 |
| `feature:domain` | 순수 코틀린 | UseCase 와 Repository **계약**만. 안드로이드 의존 없음 |
| `feature:data` | Android + Hilt | 그 계약의 구현. 응답을 도메인 모양으로 옮김 |
| `:common:network` | 공용 | 통신 스택 하나. 엔드포인트는 각 `data` 모듈이 정함 |
| `:tti:domain` | 순수 코틀린 | 무엇을 언제 재는가. 시각·저장은 포트로 위임 |
| `:jank:domain` | 순수 코틀린 | 프레임을 어떻게 묶고 언제 내보내는가. 내보낼 곳은 포트로 위임 |
| `:logging:domain` | 순수 코틀린 | 무엇을 남길 수 있는가. 전송은 포트로 위임 |
| `:featureflag:domain` | 순수 코틀린 | 어떤 플래그·실험이 있는가. 값 출처는 포트로 위임 |
| `:common:util` | Android + Hilt | 기기가 어떤 기기인가. 계약은 `DeviceInfoProvider` 하나 |

## 주요 경로 — 소장품 상세 조회

탭 한 번이 `Intent` 하나로 들어가고, 상태 하나가 돌아와 화면이 그려집니다.
그 위에 TTI 계측이 얹혀 각 구간의 시간을 따로 재고, 비즈니스 이벤트가 따로 나갑니다.

```mermaid
sequenceDiagram
    actor U as 사용자
    participant S as DetailScreen
    participant VM as DetailViewModel
    participant UC as GetRelicDetail
    participant R as RepositoryImpl
    participant API as e뮤지엄 API
    participant T as TTI 기록기
    participant B as 이벤트 기록기

    U->>S: 상세 카드 탭
    Note over S,B: 백스택 맨 위가 바뀌면 :app 이 view_enter 를 남긴다

    rect rgba(140,120,220,0.10)
    Note over S,T: 첫 컴포지션 — VIEW_CREATE
    S-->>T: VIEW_CREATE 열기
    S->>VM: ViewModel 생성 · 화면 컴포즈
    S-->>T: VIEW_CREATE 닫기
    end

    Note over S,VM: 컴포지션이 끝난 뒤 LaunchedEffect 가 돈다
    S->>VM: DetailIntent.Load(id)
    VM-->>B: relic_open{relicId}
    VM-->>T: BACKEND 열기
    VM->>UC: GetRelicDetailUseCase
    UC->>R: RelicDetailRepository
    R->>API: GET /openapi/detail?id=
    API-->>R: XML 응답
    R-->>UC: 도메인 모델로 매핑
    UC-->>VM: 상세 데이터
    VM-->>S: UiState.Success
    S-->>T: BACKEND 닫기 · VIEW_BINDING 열기
    S-->>T: 프레임 그려짐 → VIEW_BINDING 닫기
    S-->>U: 상세 화면 표시
```

> 🔍 인터랙티브 버전: [`docs/use-case-detail.html`](docs/use-case-detail.html)
> 각 메시지에 붙은 주석과 `화면 진입` · `조회` · `그리기 · 계측 완료` · `이벤트 로깅` 갈래를
> 따로 볼 수 있습니다.

**이 경로에서 지키는 것**

- 라우트 인자는 생성자가 아니라 `Intent` 로 들어갑니다 — 딥링크·백스택 복원도 같은 길을 탑니다.
- 인증키는 `ServiceKeyInterceptor` 가 붙이고, 응답 XML 은 전용 컨버터가 파싱합니다.
- 없는 id 면 저장소가 예외를 던지고 화면은 에러 상태로 떨어집니다.

## 화면 진입 시간(TTI) 계측

`:tti` 는 화면 하나가 "쓸 수 있는 상태"가 되기까지를 네 구간으로 나눠 잽니다.

| 구간 | 재는 것 |
|---|---|
| `VIEW_CREATE` | 화면 진입 → ViewModel 생성 · 첫 컴포지션 |
| `BACKEND` | 서버 요청 → 응답 |
| `VIEW_BINDING` | 데이터로 다시 그리기 시작 → 그 프레임이 실제로 그려짐 |
| `BIG_PART_LOADING` | 사진처럼 뒤늦게 채워지는 큰 덩어리 |

합계는 처음과 끝의 차이가 아니라 **구간 길이의 합**입니다. 구간 사이에는 측정하지 않는
빈 시간이 끼어들 수 있고, 그것까지 TTI 로 세면 화면이 느려진 것처럼 보입니다.

에뮬레이터 실측 예시 (Logcat 태그는 `System.out`):

```
TTI: /home          total=7021ms (VIEW_CREATE=138ms | BACKEND=1258ms | VIEW_BINDING=56ms | BIG_PART_LOADING=5569ms)
TTI: /detail        total=1292ms (VIEW_CREATE=  7ms | BACKEND= 220ms | VIEW_BINDING= 9ms | BIG_PART_LOADING=1056ms)
TTI: /search/result total= 647ms (VIEW_CREATE=  8ms | BACKEND= 635ms | VIEW_BINDING= 4ms | BIG_PART_LOADING=   0ms)
```

```bash
adb logcat | grep "TTI:"
```

## 프레임 드랍(jank) 계측

`:tti` 가 "언제 쓸 수 있게 되는가" 를 잰다면 `:jank` 는 "쓰는 동안 매끄러운가" 를 잽니다.
JankStats 가 프레임마다 부르는 콜백을 `JankReporter` 가 받아 통계로 묶습니다.

세 가지를 따로 셉니다 — 화면 하나에서 쌓인 것, 스크롤 한 구간에서 쌓인 것, 그리고 그냥 두면
놓칠 만큼 나쁜 한 프레임. 원인이 다르기 때문입니다. 화면 전체 비율은 그 화면이 무거운지를 말하고,
스크롤 구간은 목록이 무거운지를 말합니다. 한 통에 담으면 스크롤하지 않고 머문 시간이 비율을
희석해 목록 문제가 보이지 않습니다.

| 내보내는 계기 | 언제 |
|---|---|
| `PAGE_EXIT` | 화면을 떠났다 — 그 화면에서 쌓인 것을 넘긴다 |
| `SCROLL_END` | 스크롤이 멈췄다 — 그 구간만 따로 본다 |
| `THRESHOLD_EXCEEDED` | 누적 비율이 5% 를 넘었다(최소 120 프레임 모인 뒤) — 내보내고 버킷을 비운다 |
| `FROZEN_FRAME` | 한 프레임이 700ms 이상 걸렸다 — 누적과 별개로 한 건씩 |
| `BACKGROUND` | 앱이 뒤로 내려갔다 — 남은 것을 잃지 않으려고 비운다 |

표본이 쌓이기 전에는 비율을 재지 않습니다. 화면이 처음 뜨는 몇 프레임은 늘 나쁘게 나오고,
그것만으로 임계치를 넘기면 모든 화면이 매번 걸립니다.

화면 이름은 라우팅 테이블에서 한 번 등록하고, 목록은 스크롤 상태를 그대로 넘깁니다.

```kotlin
JankPage(pageName = route.path)   // :app 의 라우팅 테이블 — TTI 의 TtiPage 와 같은 자리
JankScrollWatcher(listState)      // 홈 · 검색 결과 · 보관함의 목록
```

집계는 전부 메인 스레드에서 돕니다. JankStats 의 콜백이 그 위에서 돌기 때문이고,
그래서 잠금이 없습니다 — 잠그면 그 대기가 다시 프레임을 놓치게 만듭니다.

출력 형식 (Logcat 태그는 `JANK`):

```
[SCROLL_END] page=/home frames=214 jank=17 frozen=0 ratio=7.94% avg=9ms max=112ms states={page=/home, scrolling=true}
```

```bash
adb logcat -s JANK
```

디버그 빌드만 로그로 흘리고 릴리스는 아무 일도 하지 않습니다 — 사용자 기기에서 프레임마다 도는
계측이라, 값을 받을 곳이 생기기 전까지는 비용만 남기 때문입니다.
Firebase Performance 같은 수집기를 붙이면 `JankReport` 구현 하나만 채웁니다.

## 비즈니스 이벤트 로깅

`:logging` 은 사용자가 무엇을 했는지를 남깁니다. TTI 와 달리 쌓아 두지 않습니다 —
`record` 한 건이 그대로 한 번의 전송입니다.

남길 수 있는 이벤트는 `BizEvent` 한 곳에 모여 있고 sealed 라, 카탈로그에 없는 이벤트는
아예 남길 수 없습니다. 딸리는 값도 이벤트가 생성자로 받으므로 빠뜨리면 컴파일이 멈춥니다.

| 이벤트 | 값 | 남기는 곳 |
|---|---|---|
| `ViewEnter` | `from` | 백스택 맨 위가 바뀔 때 `:app` 에서 한 번에 |
| `RelicOpen` | `relicId` | 상세 조회 시작 |
| `SaveToggle` | `relicId` · `saved` | 홈 · 검색 결과 · 상세 · 보관함 |
| `SearchSubmit` | `query` | 검색 · 검색 결과 |
| `SearchRecentClear` | 없음 | 검색 |
| `FilterApply` | `tabCode` · `optionCodes` | 검색 결과 |
| `EraSelect` | `eraCode` | 홈 |
| `LayoutToggle` | `layout` | 보관함 |
| `FlagsResolved` | `environment` · 배정 전부 | 플래그를 받아 온 직후 `:featureflag` 에서 |
| `AbExposed` | `experiment` · `variant` | 그 변형이 실제로 화면에 쓰인 자리 |

```kotlin
bizLogger.record(DetailPage.PATH, BizEvent.RelicOpen(relicId = id))
```

화면 이름은 TTI 의 `pageName` 과 같은 경로 상수입니다 — 두 지표를 같은 화면 기준으로
겹쳐 볼 수 있어야 하기 때문입니다. 같은 행동이라도 어느 화면에서 일어났는지로 갈립니다.

출력 형식 (Logcat 태그는 `System.out`):

```
BIZLOG: /home view_enter user=2b7f3c9a-4d51-4e08-9a6b-1c0f5e83d7a2 at=1789516812031 {from=null}
BIZLOG: /home era_select user=2b7f3c9a-4d51-4e08-9a6b-1c0f5e83d7a2 at=1789516819447 {eraCode=PS01}
BIZLOG: /detail relic_open user=2b7f3c9a-4d51-4e08-9a6b-1c0f5e83d7a2 at=1789516824903 {relicId=PS0100100100100119900000}
BIZLOG: /detail save_toggle user=2b7f3c9a-4d51-4e08-9a6b-1c0f5e83d7a2 at=1789516831118 {relicId=PS0100100100100119900000, saved=true}
```

```bash
adb logcat | grep "BIZLOG:"
```

## 피처 플래그 · AB 테스트

`:featureflag` 는 리모트 컨피그에서 값을 받아 플래그와 실험 배정을 읽습니다.
순수 코틀린이라 화면뿐 아니라 UseCase 도 같은 제공자를 생성자 주입으로 받습니다.

```kotlin
class GetHomeRelicsUseCase @Inject constructor(
    private val homeContentRepository: HomeContentRepository,
    private val featureFlags: FeatureFlagProvider,
) {
    suspend operator fun invoke(eraCode: String? = null) = homeContentRepository.getHomeRelics(
        eraCode = eraCode,
        pageSize = featureFlags.get(FlagKey.HomePageSize),
    )
}
```

무엇이 있는지는 `FlagKey` 한 곳에서 봅니다. 키 하나가 이름 · 타입 · 기본값을 함께 지니므로
부르는 쪽에 캐스팅도 기본값 처리도 없습니다.

| 플래그 | 타입 | 안정 | 쓰는 곳 |
|---|---|---|---|
| `NewOnboarding` | `Boolean` | 예 | 온보딩 개편 스위치 |
| `HomePageSize` | `Int` | 예 | 홈 조회 행 수 |
| `HomeBanner` | `HomeBannerVO` | 예 | 홈 배너 문구 · 링크 |
| `SearchRetryCount` | `Int` | 아니오 | 검색 재시도 |
| `FetchTimeoutMillis` | `Long` | 아니오 | 네트워크 타임아웃 |
| `RetryBackoffMillis` | `List<Long>` | 아니오 | 재시도 간격 |
| `LibraryLayoutAb` | `LibraryLayoutVariant` | 예 | 보관함 첫 보기 실험 |

**타입을 `KSerializer` 로 들고 다니는 이유**

`Class<T>` 는 제네릭을 지웁니다. `List<Long>` 을 `Class` 로 적으면 원소가 `Long` 인지 알 수 없어
원소 타입을 따로 받아야 하고, 값은 돌아오는 길에 `Any` 를 거칩니다.
`KSerializer` 는 원소 타입까지 담고 있어 원시 타입 · 리스트 · JSON 객체를 한 방법으로 읽습니다.

```kotlin
data object RetryBackoffMillis : FlagKey<List<Long>>(
    key = "retry_backoff_ms",
    serializer = ListSerializer(Long.serializer()),
    defaultValue = listOf(1_000L, 2_000L, 4_000L),
    stable = false,
)
```

읽을 때는 리모트 값을 `JsonElement` 로 모아 그 직렬화기 하나로 해석합니다.
분기마다 캐스팅하지 않으므로 선언한 타입과 어긋나는 조합을 쓸 수 없고, enum 변형이 그대로 따라옵니다.
그래서 AB 변형은 문자열이 아니라 enum 이고, 쓰는 쪽의 `when` 은 컴파일러가 검사합니다.

**안정과 비안정**

`stable` 은 "한 실행 동안 바뀌지 않아도 되는가" 입니다. 안정된 것은 `init` 이 받아 둔 값을 왕복 없이
쓰고, 아닌 것은 읽을 때마다 다시 가져오되 왕복이 실패하면 마지막으로 성공한 값을 내놓습니다.
화면 구성을 가르는 값이 도중에 바뀌면 같은 사용자가 두 화면을 다르게 보고,
운영값이 네트워크가 끊겼다고 초기값으로 되돌아가면 고쳐 둔 타임아웃이 그 자리에서 풀립니다.

**환경**

`dev` · `qa` · `prod` 는 빌드 플레이버이고, 리모트 키 꼬리로 갈립니다.
찾는 순서는 아래로만 흐릅니다 — `dev` 는 자기 값이 없으면 `qa`, 그다음 `prod` 를 주워 쓰고
`prod` 는 위를 보지 않습니다. 같은 값을 환경마다 적어 두지 않아도 되고,
`dev` 에서 켜 본 실험이 `prod` 사용자에게 새지도 않습니다.

```
home_page_size_dev → home_page_size_qa → home_page_size_prod → FlagKey.defaultValue
```

배정은 `init` 안에서 곧바로 로그로 나갑니다. 부르는 쪽에 맡기면 빠뜨릴 수 있고,
그러면 어떤 사용자가 어떤 실험에 있었는지 나중에 되짚을 수 없습니다.
사용자 식별자는 `:logging` 이 기록마다 붙입니다.

```
BIZLOG: /app flags_resolved user=2b7f3c9a-4d51-4e08-9a6b-1c0f5e83d7a2 at=1789516811004 {environment=dev, flag_new_onboarding=true, flag_home_page_size=5, flag_ab_library_default_layout=GRID}
```

지금 값 출처는 앱 안에 둔 샘플 구현입니다. `google-services.json` 을 넣고
`RemoteConfigSource` 구현만 Firebase 것으로 바꾸면 읽는 쪽 코드는 그대로입니다.


**이 모듈에서 지키는 것**

- 사용자 UUID 는 앱 실행당 하나이고, 기록기가 아니라 **건마다** 붙습니다 — 전송이 도는 사이에
  `makeUUID` 로 사용자가 바뀌어도 먼저 기록된 건은 원래 주인을 답니다.
- 전송은 한 줄로 세워 일어난 순서대로 내보냅니다. 병렬성 1 만으로는 부족합니다 —
  전송기가 네트워크에서 멈추면 다음 건이 앞질러 나갑니다.
- 실패한 건은 그대로 사라집니다. 쌓아 두는 곳이 없으므로 재시도는 전송기 구현의 몫입니다.


## 기기 정보

`:common:util` 은 기기가 어떤 기기인지를 한곳에서 읽어 줍니다. 계약은 `DeviceInfoProvider` 하나입니다.

**왜 따로 두는가**

앞의 세 계측(TTI · jank · 로깅)은 숫자만 남깁니다. 그 숫자가 왜 그렇게 나왔는지는 기기 사정을
함께 봐야 압니다.

- 성능이 나쁜 기기의 TTI 가 섞이면 통계가 깨집니다. 저사양 기기 몇 대의 `BIG_PART_LOADING` 이
  평균을 끌어올리면, 아무것도 고치지 않았는데 지표가 나빠지고 반대로 고쳐도 표가 나지 않습니다.
  `isLowRamDevice` · `getCpuCoreCount` · `getMemoryInfo` 로 코호트를 갈라 놓아야 같은 기기끼리 비교됩니다.
- 대역폭이 좁을 때 느린 이미지 로딩을 우리 코드 문제로 읽을 여지가 있습니다.
  `BIG_PART_LOADING` 이 5초면 사진을 늦게 그린 것처럼 보이지만, `getNetworkType` 이 `CELLULAR` 이고
  `getDownstreamBandwidthKbps` 가 바닥이면 그것은 회선이 한 일입니다. 이 값이 없으면
  있지도 않은 원인을 코드에서 찾게 됩니다.
- 같은 이유로 절전 모드(`isPowerSaveMode`)와 발열(`getThermalStatus`)도 함께 봅니다 —
  둘 다 OS 가 클럭을 내리는 구간이라, 그 표본을 평균에 섞으면 개선도 퇴행도 보이지 않습니다.

항목 하나에 함수 하나입니다. 덩어리로 내보내지 않는 이유는 부르는 쪽이 필요한 것만
가져가게 하려는 것입니다 — 크래시 리포트는 지문과 ABI 만, 프레임 통계는 주사율과 발열만 씁니다.

| 주기 | 항목 | 어떻게 |
|---|---|---|
| 세션 1회 | 제조사 · 모델 · OS · ABI · 지문 · 앱 버전 · 설치 경로 · 저사양 여부 · 코어 수 · 저장공간 · 통신사 · 식별자 | 구현이 항목마다 `by lazy` 로 한 번만 읽는다 |
| 변경 시 | 창 크기 · 밀도 · 주사율 · 다크모드 · 로케일 · 글꼴 배율 · 방향 | 부를 때마다 다시 읽는다 — 캐시하면 거짓이 된다 |
| 이벤트마다 | 메모리 · 배터리 · 충전 · 절전 · 발열 · 연결 타입 · 종량제 · 대역폭 | 순간의 값이라 역시 캐시하지 않는다 |

```kotlin
class SomeReporter @Inject constructor(
    private val deviceInfo: DeviceInfoProvider,
) {
    fun onSlowScreen(pageName: String) {
        if (deviceInfo.isLowRamDevice() || deviceInfo.isPowerSaveMode()) return  // 다른 통에 센다
        ...
    }
}
```

주사율은 세션 1회가 아닙니다. 가변 주사율 기기는 120 으로 시작해 배터리를 아끼려 60 으로
내려갑니다. 처음 값을 들고 있으면 그 뒤의 프레임을 두 배 너그러운 기준으로 재게 되므로,
`getFrameBudgetMillis()` 는 부를 때마다 지금 주사율에서 다시 계산합니다.

식별자는 우리가 만든 UUID 가 기본입니다. `ANDROID_ID` 는 서명 키 + 유저 단위로 나뉘고
초기화·재설치에서 달라질 수 있어 단독으로는 믿을 수 없습니다. 설치 ID 는 첫 실행에 만들어
`:common:datastore` 의 `device_preferences` 에 두고, 앱을 지우면 함께 사라집니다.

권한은 `ACCESS_NETWORK_STATE` 하나입니다(모듈 매니페스트가 선언합니다).
통신사 이름도 권한이 필요 없는 `networkOperatorName` 만 읽습니다 —
가입자 식별에 닿는 값은 `READ_PHONE_STATE` 를 부르고, 이 모듈은 그 권한을 요구하지 않습니다.

빌드 변형(`BuildConfig.BUILD_TYPE` · `FLAVOR`)만은 이 모듈이 스스로 읽지 못합니다.
`BuildConfig` 는 그것을 생성한 모듈의 것이라 플레이버를 가진 `:app` 이 DI 로 꽂아 줍니다
(`DeviceInfoAppModule`). `:featureflag` 의 환경(`APP_ENV`)이 같은 이유로 같은 길을 지납니다.

지금은 읽는 쪽만 있습니다. 계측 · 로깅 기록에 이 값을 붙이는 배선은 아직입니다 —
무엇을 어느 이벤트에 붙일지(세션 1회는 세션 시작에 한 번, 순간 값은 퍼포먼스 이벤트에만)를
정한 뒤에 `:logging` · `:tti` · `:jank` 쪽에서 가져다 씁니다.

## 빌드

인증키와 주소는 소스에 두지 않고 `local.properties` 에서만 읽습니다
(`common/network/build.gradle.kts` 가 `BuildConfig` 로 굽습니다).

```properties
EMUSEUM_BASE_URL=https://apis.data.go.kr/.../
EMUSEUM_SERVICE_KEY=발급받은_인증키
```

환경은 빌드 플레이버로 고릅니다.

```bash
./gradlew :app:assembleDevDebug
./gradlew :app:assembleQaDebug
./gradlew :app:assembleProdRelease

./gradlew :tti:domain:test
./gradlew :jank:domain:test
./gradlew :logging:domain:test
./gradlew :featureflag:domain:test
./gradlew :common:util:testDebugUnitTest
```

---

다이어그램 원본은 [`docs/diagrams/`](docs/diagrams) 의 JSON 이고,
[archify](https://github.com/tt-a1i/archify) 로 `docs/*.html` 을 만듭니다.
