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





