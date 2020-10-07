Servlet Applications
=====================
Spring Security 는 Servlet Filter 를 사용한 Servlet Container 를 통합했다.  
이 말의 의미는 Servlet Container 를 사용하는 어떤 Application 과도 사용할 수 있다는 것 이다.  
다시말해, Spring Security 를 사용하기 위해서 Servlet 기반 Application 을  
굳이 Spring 을 사용하지 않아도 된다는 것 이다.  

# Hello Spring Security
이 섹션에서는 Spring Boot 에서 어떻게 Spring Security 를 설정하는  
간단한 부분만 다룬다.    

_Spring Boot + Security Application 의 간략한 참고 소스를 다운로드 하려면
[**이곳**](https://start.spring.io/starter.zip?type=maven-project&language=java&packaging=jar&jvmVersion=1.8&groupId=example&artifactId=hello-security&name=hello-security&description=Hello%20Security&packageName=example.hello-security&dependencies=web,security) 을 클릭해 다운받자_  

## Updating Dependencies
Maven 은 [**이곳**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#getting-maven-boot), Gradle 은 [**이곳**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#getting-gradle-boot) 을 참조하여 디펜던시 설정하면 된다.  

## Spring Boot Auto Configuration
Spring Boot 는 아래의 3가지를 자동으로 진행한다.  
* springSecurityFilterChain 이라는 이름의 Bean 의 servlet filter 를 생성하는  
Spring Security 기본 환경설정을 활성화한다. springSecurityFilterChain 빈은  
Application URL 들을 보호하고, Username 과 Password 의 검증, 로그인 폼으로 리다이렉트 등의  
우리 Application 의 전체 보안을 책임진다.  

* UserDetailsService Bean 을 생성하며 이 Bean 은 user 라는 이름의 Username 과 랜던으로 생성되는 Password 를  
콘솔에 로깅한다.  

* 모든 Request 에 대해 springSecurityFilterChain 이라는 이름의 Bean 으로 필터를 Servlet Container 에 등록한다.  

Spring Boot 는 많은 설정을 하지 않지만 많은 기능을 제공한다.  
아래의 내용을 통해 어떤 기능을 제공하는지 보자.  

* Application 과 통신하기 위해 User 에게 인증을 요구한다.  

* 기본 로그인 폼을 제공한다.  

* 폼 기반 인증을 하기 위해 유저에게 user 라는 값의 Username 과 콘솔 로그에 랜덤하게  
생선되는 값의 Password 알려준다.  
(Spring Boot 에 Spring Security 디펜던시를 걸고 Application 실행시  
콘솔 로그에 찍히는 Password 예 : 8e557245-73e2-4286-969a-ff57fe3263368e557245-73e2-4286-969a-ff57fe326336)  

* Password 저장소를 BCrypt 기반으로 보호해줌.

* 로그아웃을 제공함.

* CSRF 공격을 방지해줌.

* [**Session Fixation**](https://en.wikipedia.org/wiki/Session_fixation) 을 막아줌.

* 보안 헤더 통합적으로 제공
    * HSTS(HTTP Strict Transport Security) Header
    * X-Content-Type-Options Header
    * Cache Control   
    (우리 Application 의 static Resource 에 대해 설정해서 오버라이딩 가능)
    * X-XSS-Protection Header
    * X-Frame-Options Header
    
* Servlet API 메서드 통합적으로 제공
    * [**HttpServletRequest#getRemoteUser()**](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#getRemoteUser())
    * [**HttpServletRequest.html#getUserPrincipal()**](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#getUserPrincipal())
    * [**HttpServletRequest.html#isUserInRole(java.lang.String)**](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#isUserInRole(java.lang.String))
    * [**HttpServletRequest.html#login(java.lang.String, java.lang.String)**](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#login(java.lang.String,%20java.lang.String))
    * [**HttpServletRequest.html#logout()**](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#logout())

# Servlet Security: The Big Picture
이번 장에서는 Servlet 기반의 Application 에서의 Spring Security 의 고수준 아키텍쳐를 설명한다.  
이 장에서는 [**인증**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#servlet-authentication), [**인가**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#servlet-authorization), [**악용 방지**](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#servlet-exploits) 섹션에 대한 높은 이해도를 바탕으로 풀어나간다.  

## A Review of Filters
Spring Security 의 Servlet 지원은 Servlet Filter 를 기반으로 한다.  
Filter 의 역할을 우선적으로 살펴보는 것이 도움이 될 것 이다.  
아래의 그림은 HTTP Request 에 대한 처리를 계층적으로 보여준다.  

<img width="258" alt="Security FilterChain" src="https://docs.spring.io/spring-security/site/docs/current/reference/html5/images/servlet/architecture/filterchain.png">  

Client 는 Application 에 Request 를 보낸다, 그리고 컨테이너는 Request URI 에 기반한 HttpServletRequest 를  
처리하기 위해 Servlet 과 Filter 를 포함하는 FilterChain 을 생성한다.  
Spring MVC Application 에서 Servlet 은 DispatcherServlet 의 Instance 이다.  
하나의 Servlet 이 하나의 HttpServletRequest 와 HttpServletResponse 를 처리한다.  
하지만 두개 이상의 Filter 를 사용하여 아래의 역할을 한다.  

* downstream Filter 혹은 서블릿 호출되지 않도록 방지한다. 이 인스턴스에서는  
Filter 는 일반적으로 HttpServletResponse 를 만든다.      

* downstream Filter 그리고 Servlet 에 사용되는 HttpServletRequest 나 HttpServletResponse 를  
수정한다.  

Filter 의 힘은 전달된 FilterChain 을 통해 나온다.  

* FilterChain Usage Example  
```
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    // do something before the rest of the application
    chain.doFilter(request, response); // invoke the rest of the application
    // do something after the rest of the application
}
```

Filter 의 downstream Filter 와 Servlet 에만 끼치는 영향력 때문에,  
각 Filter 의 순서는 아주 중요하다.  

## DelegatingFilterProxy
Spring Security 는 Servlet 컨테이너의 라이프 사이클과 Spring 의 ApplicationContext 의 연결고리 역할을 하는  
DelegatingFilterProxy 라는 이름의 Filter 를 제공한다.  
Servlet 컨테이너는 Servlet 의 표준을 따르는 Filter 를 등록하는 것을 허용하지만,  
Spring 에서 정의된 Bean 을 알지못한다.  
DelegatingFilterProxy 는 Servlet 컨테이너의 메카니즘을 활용하여 등록된다,  
그러나 모든 역할을 Filter 를 구현한 Spring Bean 에게 위임한다. 

아래의 그림은 Filter 와 FilterChain 에 어떻게 DelegatingFilterProxy 가 적용되어있는지 나타낸다.  
<img width="258" alt="DelegatingFilterProxy in FilterChain" src="https://docs.spring.io/spring-security/site/docs/current/reference/html5/images/servlet/architecture/delegatingfilterproxy.png">  

DelegatingFilterProxy 는 ApplicationContext 에 존재하는 Bean Filter0 을 바라보고,  
BeanFilter0 를 호출한다.  

아래의 코드는 DelegatingFilterProxy 의 슈도코드이다.  

* DelegatingFilterProxy Pseudo Code
```
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    // Lazily get Filter that was registered as a Spring Bean
    // For the example in DelegatingFilterProxy delegate is an instance of Bean Filter0
    Filter delegate = getFilterBean(someBeanName);
    // delegate work to the Spring Bean
    delegate.doFilter(request, response);
}
```

