Authentication
==============

Spring Security 는 다양한 Authentication(인증) 방식을 지원한다.  
Authentication 이란 특정 리소스에 접근하려는 사람이 누구인지  
어떻게 밝혀낼 것이냐에 대한 것이다.  

일반적인 Authentication 의 방식은 사용자로부터 username(ID) 과 Password 를  
입력받는 것이다.

한번 Authentication 이 되면 누구인지에 대한 식별이 되었기 때문에  
해당 유저가 어떤 권한을 가지고 있는지 검사가 가능해진다.


# 1. Authentication Support
Spring Security 는 User 를 Authentication 하기 위한 지원을 한다.    
이 부분은 글을 풀어나가며 알게될 것 이다.

# 2. Password Storage
Spring Security 의 PasswordEncoder Interface 는 Password 를  
단방향으로 변환시켜 Password 가 보안적이게 저장되도록 한다.  

제공되는 PasswordEncoder 가 단방향 변환을 하기 때문에 양방향일때는 사용하지 않는다.  

일반적으로 PasswordEncoder 는 Password 를 저장할 때 사용하는데,  
이 Password 는 Authentication 시점에 유저로부터 입력된 Password 가 올바른 Password 인지  
판단하기 위해서 사용한다.  
(즉, 비밀번호를 저장할 때 유저의 인증을 위해서만 사용되기 때문에 단방향 암호화를 통해서 저장하는 것이  
맞다는 것)

# 3. Password Storage History
수년간 Password 를 정자하는 메커니즘은 진화해왔다.  
Password 를 저장하기 시작했을 땐 평문으로 저장했다. 이렇게 평문으로 저장하는 방식은 Password 를 저장한 곳에  
접근하기 위해선 인증된 자격이 있어야 가능했기 때문에 안전해 보였다.  
(DB 에 저장하고 DB 에 해당 정보를 조회하려면 DB 에 접근이 가능해야하기때문)

하지만 악의적인 유저들은 SQL Injection 등과 같은 공격을 통해서 사용자들의 ID 및 Password 를 알아낼 수 있는  
방법을 찾아 냈다. 이렇게 점점 더 사용자들의 크레덴셜(ID 및 Password 등)이 공개적으로 됨에 따라,  
보안 전문가들은 유저들의 Password 를 보호하기 위해 더 많은 행동을 취해야한다는 것을 깨달았다.  

그래서 개발자들은 SHA-256 과 같이 단방향 암호화를 수행한 후 Password 를 저장하도록 권장받았다.  
어떤 유저가 인증을 시도할때(로그인을 시도할 때), 암호화되어 저장된 Password 와 유저가 입력한 Password 를  
암호화 한 값과 비교하여 일치하는지 여부를 확인하면 된다.  

이것이 의미하는 것은 우리의 시스템이 단방향으로 암호화 된 Password 만 저장하면 된다는 것이다.  
이렇게되면 앞선 상황과 같이 사용자들의 Password 가 유출되더라도 단방향으로 암호화된 Password 가 유출된 뿐이다.

단방향 암호화이며 계산적으로 주어진 해시를 바탕으로 Password 를 추론해내는데 어렵기때문에,  
시스템의 각각의 암호를 알아내는 것은 그만한 가치가 없다.  

이렇게 단방향 암호화를 적용한 시스템을 무력화하기위해 악의적인 유저들은 [**Rainbow Tables**](https://en.wikipedia.org/wiki/Rainbow_table) 와 같은  
lookup Table 을 만들어 사용하기로 생각했다.매번 각각의 Password 에 대한 추측을 하기보단, Password 를 계산하고  
lookup Table 에 저장하는 것이다.  

Rainbow Tables 의 효과를 약하게 하기 위해, 개발자들은 salted Password 를 사용하도록 권장받고있다.  
(salted Password 란 말그대로 소금이라고 불리는 무작위 문자열을 Password 를 암호화하기전에 붙여서  
 동일한 비밀번호라도 같은 해시 값(암호화된 값)을 가지지않도록 하여 lookup Table 을 무력화 하는 것이라고 한다.)  
 
입력된 Password 를 바로 단방향 암호화 함수에 넣지말고, 모든 유저의 Password 에 대해 랜덤한 문자를 추가하는 것이다.  
Salt(랜덤한 문자) 와 유저의 Password 는 단 하나의 해시 값만을 갖는 해시 함수에 넣어서 돌려야한다.  

그래서 사용자가 인증(로그인)을 시도할 때 단방향 암호화 되어 저장된 비밀번호와 사용자가 입력한 비밀번호와  
저장되어있는 Salt(랜덤한 문자)를 같이 비교해야한다.  

유일한 Salt 가 의미하는 것은 단방향 암호화 된 비밀번호와 Salt 는 매번 해시 값이 다르기 때문에  
더이상 Rainbow Tables 가 효력이 없다는 것을 의미한다.

현대에 이르러 우리는 SHA-256 과 같은 해싱 알고리즘이 더이상 안전하지 않다는 것을 깨달았다.  
왜냐면 요즘 하드웨어는 1초에 몇십억번의 해싱 계산을 수행할 수 있기 때문이다.  
이것은 각각의 암호를 쉽게 해독할 수 있나는 뜻이다.  

이제 개발자들에게 Password 를 저장할 때 adaptive 단방향 암호화를 사용하도록 권장하고 있다.  
adaptive 단방향 암호화 기능의 경우 CPU, Memory 와 같은 리소스에 집약적이다.  
adaptive 단방향 암호화 기능을 사용하면 하드웨어의 발달에 따라 증가하는 "작업요소(work factor)" 를 구성할 수 있다.  
(무슨말인지 정확히는 모르겠다.)  

"작업요소" 는 우리의 시스템에서 약 1초동안 Password 검증을 하도록 조절되어야 한다.  
이러한 트레이드 오프는 Password 를 공격자가 해독하기 어렵게 만들며, 우리 시스템의 부담도 크지않다.

Spring Security 는 "작업요소" 를 제공하는 좋은 시작점을 제공하기위해 노력해왔다.  
하지만 Spring Security 를 사용하는 개발자들에게 "작업요소"를 자신의 시스템에 맞게 커스텀하여  
사용하는 것을 권장한다.(시스템마다 성능이 다르기 때문)  
adaptive 단방향 암호화 함수의 예로는 [**bcrypt**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authentication-password-storage-bcrypt) 
, [**PBKDF2**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authentication-password-storage-pbkdf2)
, [**scrypt**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authentication-password-storage-scrypt) 
그리고 [**argon2**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#authentication-password-storage-argon2)
가 있다.  

adaptive 단방향 함수는 시스템 리소스를 사용하기 때문에, 매 요청마다 Username(ID) 과 Password 를  
검증하는 것은 서비스의 성능을 저하시킬 수 있다. Spring Security(혹은 다른 라이브러리에도) 에는 Password 의  
검증을 빠르게 해주는 것이 없다.(보안은 유효성 검사에 대한 시스템 리소스를 사용하면서 얻어지는 것이니까)  

서비스를 개발하는 개발자들에게 Username & Password 와 같은 Long Term Credentials 에서  
session 이나 OAuth Token 과 같은 Short Term Credentials 로 바꿀 것을 권장한다.  
Short Term Credentials 는 보안 유출 걱정없이 빠르게 검증이 되기 떄문이다.



 

# 4. DelegatingPasswordEncoder
Spring Security 5.0 이전의 기본 PasswordEncoder 는 Password 를 암호화하지 않고  
평문으로 저장하는 NoOpPasswordEncoder 였다.  

앞서 말한 Password Storage History 를 보면 이제 BCryptPasswordEncoder 와 같은 PasswordEncoder 가  
기본 PasswordEncoder 라는 것을 짐작할 수 있을 것 이다.  

하지만 이렇게 기본 PasswordEncoder 가 바뀌면 아래와 같은 3가지 문제점이 있다.
* 이전에 평문으로 저장된 Password 를 쉽게 마이그레이션 할 수 없다는 점

* 이러한 방식으로 변경해서 저장하더라도 나중에 더 좋은 방식이 나오면 다시 바뀐다는 점

* Spring Security 는 프레임워크로써 자주 변경할 수 없다는 점

그래서 Spring Security 는 DelegatingPasswordEncoder 라는 것을 문제의 해결책으로 내놓았고  
아래의 해결책을 제시한다.  
* 현재 Password 를 저장하는 저장소(DB)에 맞는 인코딩 방식으로 암호화 되었는지 확인할 수 있다.

* Password 를 검증하는데 레거시 형식이나 현대 방식을 모두 사용할 수 있게 한다.

* 미래에 더 좋은 인코딩 방식에 대한 업그레이드를 지원한다.

우리는 PasswordEncoderFactories 를 사용해 DelegatingPasswordEncoder 를 사용할 수 있다.

```

PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

```

혹은 내가 원하는 Instance 를 생성할 수도 있다.

```

String idForEncode = "bcrypt";
Map encoders = new HashMap<>();
encoders.put(idForEncode, new BCryptPasswordEncoder());
encoders.put("noop", NoOpPasswordEncoder.getInstance());
encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
encoders.put("scrypt", new SCryptPasswordEncoder());
encoders.put("sha256", new StandardPasswordEncoder());

PasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);

```



  

# 5. Password Storage Format
일반적으로 저장소에 저장되는 Password 는 아래와 같이 생겼다.

```
{id}encodedPassword
```

여기서 id 는 Password 가 저장당시에 어떤 PasswordEncoder 로 인코딩 되었는지 나타내는 것 이며,  
encodedPassword 는 PasswordEncoder 를 통해 인코딩 된 Password 이다.  

그리고 id 는 시작과 끝에 중괄호({}) 로 표시되어 있어야 한다.   
id를 찾을 수 없으면 id 값은 null 이 된다.  
아래의 코드는 "password" 라는 값을 각 id 에 해당하는 PasswordEncoder 를 활용해 인코딩한 값이다.

```

{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG 
{noop}password 
{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc 
{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=  
{sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0 

```

첫번째 줄의 PasswordEncoder id는 몰까요~~? 그거슨 바로 bcrypt 그리고 bcrypt 를 방식으로 인코딩된  
"password" 라는 문자열은 $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG  
따라서 BCryptPasswordEncoder 를 사용해서 저장되엇고, 나중에 유저로부터 Password 를 입력받아 일치 여부를 검증할 때  
요 PasswordEncoder 를 사용해야겠지?? 

그리고 두번째 줄의 PasswordEncoder id 는 noop 이다. 따라서 저장시에 NoOpPasswordEncoder 를 사용했고,  
평문으로 저장되었다.

나머지 PasswordEncoder id pbkdf2, scrypt, sha256 은 앞서 첫번째, 두번째 줄의 설명과 일맥상통한다.  

_몇몇 개발자들은 PasswordEncoder id 와 같은 storage format 이 유출되는 것이 걱정이 될 수 있다.  
하지만 Password 저장은 해당 알고리즘에만 의존하고 있지 않기 때문에 심각하지 않다.  
그리고 공격자들은 대부분 암호화된 문자열의 prefix 를 보고 어떤 알고리즘인지 쉽게 유추가 가능하다.  
(예를들어 BCrypt 로 암호화되어 있는 경우 $2a$ 와 같이 시작한다.)_  



 
 
# 6. Password Encoding
