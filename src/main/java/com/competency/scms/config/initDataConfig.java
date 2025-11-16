package com.competency.scms.config;

import com.competency.scms.domain.competency.AssessmentSection;
import com.competency.scms.domain.counseling.*;
import com.competency.scms.domain.noncurricular.mileage.*;
import com.competency.scms.domain.noncurricular.operation.*;
import com.competency.scms.domain.noncurricular.program.*;
import com.competency.scms.domain.user.User;
import com.competency.scms.domain.user.UserRole;
import com.competency.scms.repository.competency.AssessmentSectionRepository;
import com.competency.scms.repository.counseling.*;
import com.competency.scms.repository.noncurricular.mileage.MileageRecordRepository;
import com.competency.scms.repository.noncurricular.operation.ProgramApplicationRepository;
import com.competency.scms.repository.noncurricular.program.ProgramRepository;
import com.competency.scms.repository.user.UserRepository;
import com.competency.scms.repository.DepartmentRepository;
import com.competency.scms.domain.Department;

import java.time.DayOfWeek;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class initDataConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CounselingReservationRepository counselingReservationRepository;
    private final CounselorRepository counselorRepository;
    private final CounselingSubFieldRepository counselingSubFieldRepository;
    private final CounselingScheduleRepository counselingScheduleRepository;
    private final ProgramRepository programRepository;
    private final MileageRecordRepository mileageRecordRepository;
    private final SatisfactionQuestionRepository satisfactionQuestionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final ProgramApplicationRepository programApplicationRepository;
    private final AssessmentSectionRepository assessmentSectionRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final com.competency.scms.repository.competency.CompetencyRepository competencyRepository;

    private User getUser(int userNum) {
        return userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. userNum=" + userNum));
    }

    private Long getUserId(int userNum) {
        return getUser(userNum).getId();
    }

    @Override
    public void run(String... args) throws Exception {

        if(userRepository.count() > 0) {
            log.info("➡️ 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        Department deptSysAdmin        = ensureDept("SYS_ADMIN", "시스템 관리자");
        Department deptNcpAdmin        = ensureDept("NCP_ADMIN", "비교과프로그램 관리자");
        Department deptNcpOperator     = ensureDept("NCP_OPERATOR", "비교과프로그램 운영자");
        Department deptCounselAdmin    = ensureDept("COUNSEL_ADMIN", "상담 관리자");
        Department deptCompetencyAdmin = ensureDept("COMPETENCY_ADMIN", "역량관리 관리자");
        Department deptCounselCenter   = ensureDept("STUDENT_COUNSEL_CENTER", "학생상담센터");

        log.info("▶️ 테스트 데이터 초기화 시작");

        // 최고 관리자 3명 (SUPER_ADMIN)
        userRepository.save(User.builder().role(UserRole.SUPER_ADMIN).userNum(100001).name("이현우").email("leehyunwoo@pureum.ac.kr").phone("010-2363-9792")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1965, 7, 27)).department(deptSysAdmin).build());
        userRepository.save(User.builder().role(UserRole.SUPER_ADMIN).userNum(100002).name("임예린").email("limyerin@pureum.ac.kr").phone("010-2390-6079")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1983, 5, 24)).department(deptSysAdmin).build());
        userRepository.save(User.builder().role(UserRole.SUPER_ADMIN).userNum(100003).name("조은우").email("choeunwoo@pureum.ac.kr").phone("010-9926-4095")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1968, 11, 10)).department(deptSysAdmin).build());

        // 비교과프로그램 관리자 1명 (NONCURRICULAR_ADMIN)
        userRepository.save(User.builder().role(UserRole.NONCURRICULAR_ADMIN).userNum(110001).name("박태현").email("parktaehyun@pureum.ac.kr").phone("010-4205-3849")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1961, 3, 15)).department(deptNcpAdmin).build());

        // 비교과프로그램 운영자 1명 (NONCURRICULAR_OPERATOR)
        userRepository.save(User.builder().role(UserRole.NONCURRICULAR_OPERATOR).userNum(140001).name("임도윤").email("limdoyoon@pureum.ac.kr").phone("010-7136-5442")
                .password(passwordEncoder.encode(("operator123"))).birthDate(LocalDate.of(1972, 2, 13)).department(deptNcpOperator).build());

        // 상담 관리자 1명 (COUNSELING_ADMIN)
        userRepository.save(User.builder().role(UserRole.COUNSELING_ADMIN).userNum(120001).name("윤지훈").email("yoonjihun@pureum.ac.kr").phone("010-7316-9474")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1965, 12, 18)).department(deptCounselAdmin).build());

        // 역량관리 관리자 1명 (COMPETENCY_ADMIN)
        userRepository.save(User.builder().role(UserRole.COMPETENCY_ADMIN).userNum(130001).name("강지윤").email("kangjiyun@pureum.ac.kr").phone("010-3399-5747")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1971, 12, 4)).department(deptCompetencyAdmin).build());

        // 심리상담 서브필드
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.PSYCHOLOGICAL).subfieldName("스트레스 관리").description("스트레스 관리 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.PSYCHOLOGICAL).subfieldName("불안 상담").description("불안 관련 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.PSYCHOLOGICAL).subfieldName("우울감 상담").description("우울감 관련 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.PSYCHOLOGICAL).subfieldName("대인관계 상담").description("대인관계 문제 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.PSYCHOLOGICAL).subfieldName("학업 스트레스").description("학업 스트레스 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.PSYCHOLOGICAL).subfieldName("기타").description("기타 심리상담").build());

        // 진로상담 서브필드
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.CAREER).subfieldName("진로탐색").description("진로 방향 설정 및 탐색").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.CAREER).subfieldName("전공선택").description("전공 선택 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.CAREER).subfieldName("진로계획").description("진로 계획 수립").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.CAREER).subfieldName("적성검사").description("적성검사 및 분석").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.CAREER).subfieldName("미래준비").description("미래 준비 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.CAREER).subfieldName("기타").description("기타 진로상담").build());

        // 취업상담 서브필드
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.EMPLOYMENT).subfieldName("일반 서류면접").consultingType(CounselingSubField.ConsultingType.INTERVIEW).description("일반 서류면접 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.EMPLOYMENT).subfieldName("외국계").consultingType(CounselingSubField.ConsultingType.INTERVIEW).description("외국계 기업 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.EMPLOYMENT).subfieldName("이공계").consultingType(CounselingSubField.ConsultingType.INTERVIEW).description("이공계 취업 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.EMPLOYMENT).subfieldName("콘텐츠엔터").consultingType(CounselingSubField.ConsultingType.INTERVIEW).description("콘텐츠엔터 취업 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.EMPLOYMENT).subfieldName("공기업").consultingType(CounselingSubField.ConsultingType.INTERVIEW).description("공기업 취업 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.EMPLOYMENT).subfieldName("임원면접").consultingType(CounselingSubField.ConsultingType.INTERVIEW).description("임원면접 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.EMPLOYMENT).subfieldName("국문 이력서 또는 자기소개서").consultingType(CounselingSubField.ConsultingType.WRITTEN_EDITING).description("국문 이력서 또는 자기소개서 첨삭").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.EMPLOYMENT).subfieldName("영문 이력서 또는 자기소개서").consultingType(CounselingSubField.ConsultingType.WRITTEN_EDITING).description("영문 이력서 또는 자기소개서 첨삭").build());

        // 학습상담 서브필드
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.ACADEMIC).subfieldName("학습방법 상담").description("학습방법 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.ACADEMIC).subfieldName("시간관리 상담").description("시간관리 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.ACADEMIC).subfieldName("시험준비 상담").description("시험준비 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.ACADEMIC).subfieldName("집중력 향상").description("집중력 향상 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.ACADEMIC).subfieldName("학습동기 부여").description("학습동기 부여 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder().counselingField(CounselingField.ACADEMIC).subfieldName("기타").description("기타 학습상담").build());

        log.info("✅ 상담 서브필드 초기 데이터 27건이 생성되었습니다.");

        // 상담사 12명 (학생상담센터)
        User counselorUser1 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150001).name("정준호").email("jungjoonho@pureum.ac.kr").phone("010-3191-1123")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1983, 11, 14)).department(deptCounselCenter).build());
        User counselorUser2 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150002).name("조수빈").email("chosubin@pureum.ac.kr").phone("010-9053-2777")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1968, 12, 5)).department(deptCounselCenter).build());
        User counselorUser3 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150003).name("강준호").email("kangjoonho@pureum.ac.kr").phone("010-8022-6241")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1988, 5, 19)).department(deptCounselCenter).build());
        User counselorUser4 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150004).name("강현우").email("kanghyunwoo@pureum.ac.kr").phone("010-2701-1701")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1989, 4, 24)).department(deptCounselCenter).build());
        User counselorUser5 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150005).name("최하은").email("choihaeun@pureum.ac.kr").phone("010-3882-5110")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1984, 4, 19)).department(deptCounselCenter).build());
        User counselorUser6 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150006).name("임서연").email("limseoyeon@pureum.ac.kr").phone("010-6770-2619")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1968, 8, 3)).department(deptCounselCenter).build());
        User counselorUser7 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150007).name("박지민").email("parkjimin@pureum.ac.kr").phone("010-8274-4740")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1988, 9, 13)).department(deptCounselCenter).build());
        User counselorUser8 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150008).name("장민수").email("jangminsu@pureum.ac.kr").phone("010-7510-1526")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1974, 11, 17)).department(deptCounselCenter).build());
        User counselorUser9 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150009).name("김지연").email("kimjiyeon@pureum.ac.kr").phone("010-3820-1250")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1971, 9, 3)).department(deptCounselCenter).build());
        User counselorUser10 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150010).name("정유진").email("jungyujin@pureum.ac.kr").phone("010-8174-9986")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1975, 9, 16)).department(deptCounselCenter).build());
        User counselorUser11 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150011).name("최예린").email("choiyerin@pureum.ac.kr").phone("010-5069-1842")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1970, 10, 10)).department(deptCounselCenter).build());
        User counselorUser12 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150012).name("김민수").email("kimminsu@pureum.ac.kr").phone("010-7556-2469")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1984, 9, 16)).department(deptCounselCenter).build());
        User counselorUser13 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150013).name("이서준").email("leeseojun@pureum.ac.kr").phone("010-4521-8763")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1979, 3, 22)).department(deptCounselCenter).build());
        User counselorUser14 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150014).name("박민지").email("parkminji@pureum.ac.kr").phone("010-6789-3214")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1986, 7, 8)).department(deptCounselCenter).build());
        User counselorUser15 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150015).name("최영수").email("choiyoungsu@pureum.ac.kr").phone("010-8523-9641")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1982, 11, 30)).department(deptCounselCenter).build());
        User counselorUser16 = userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150016).name("정하윤").email("junghayun@pureum.ac.kr").phone("010-7412-5896")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1985, 2, 14)).department(deptCounselCenter).build());

        // 학생 데이터 50명 (STUDENT)
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213901).name("김서윤").email("jsua86268@gmail.com").phone("010-2857-1311")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 8, 5)).department(ensureDept("KOREAN_LANG", "국어국문학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212802).name("이준호").email("20212802@school.edu").phone("010-4135-9920")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 4, 24)).department(ensureDept("PSYCHOLOGY", "심리학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20214503).name("박지민").email("20214503@school.edu").phone("010-3182-7654")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 7, 28)).department(ensureDept("COMPUTER_ENGINEERING", "컴퓨터공학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212904).name("최민수").email("20212904@school.edu").phone("010-8754-2231")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 4, 9)).department(ensureDept("BUSINESS_ADMIN", "경영학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20214405).name("윤다인").email("20214405@school.edu").phone("010-3356-4881")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 5, 24)).department(ensureDept("LIFE_SCIENCE", "생명과학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212206).name("정하늘").email("20212206@school.edu").phone("010-7674-5800")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 6, 10)).department(ensureDept("PUBLIC_ADMIN", "행정학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211707).name("오예린").email("20211707@school.edu").phone("010-4482-3107")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 8, 3)).department(ensureDept("ENGLISH_LANG", "영어영문학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210808).name("안지훈").email("20210808@school.edu").phone("010-5193-2750")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 4, 27)).department(ensureDept("ELECTRONIC_ENGINEERING", "전자공학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213109).name("송수진").email("20213109@school.edu").phone("010-7352-9486")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 5, 24)).department(ensureDept("DESIGN", "디자인학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213710).name("김도윤").email("20213710@school.edu").phone("010-8692-5143")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 5, 9)).department(ensureDept("PHYSICS", "물리학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212711).name("김하은").email("20212711@school.edu").phone("010-2791-6109")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 5, 19)).department(ensureDept("HISTORY", "역사학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210912).name("문현우").email("20210912@school.edu").phone("010-6842-1108")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 8, 4)).department(ensureDept("MECHANICAL_ENGINEERING", "기계공학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210213).name("유채린").email("20210213@school.edu").phone("010-9372-8013")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 6, 16)).department(ensureDept("SOCIAL_WELFARE", "사회복지학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213414).name("장태현").email("20213414@school.edu").phone("010-1328-6452")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 4, 25)).department(ensureDept("ACCOUNTING", "회계학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211215).name("윤소연").email("20211215@school.edu").phone("010-8070-3291")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 5, 18)).department(ensureDept("CHEMISTRY", "화학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20214216).name("배정우").email("20214216@school.edu").phone("010-5823-9910")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 7, 22)).department(ensureDept("INDUSTRIAL_ENGINEERING", "산업공학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20214617).name("김수빈").email("20214617@school.edu").phone("010-3945-7482")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 8, 23)).department(ensureDept("VISUAL_DESIGN", "시각디자인학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212318).name("이동건").email("20212318@school.edu").phone("010-9512-6640")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 4, 15)).department(ensureDept("POLITICAL_SCIENCE", "정치외교학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213619).name("박예지").email("20213619@school.edu").phone("010-4049-2738")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 8, 8)).department(ensureDept("PHILOSOPHY", "철학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210320).name("손우진").email("20210320@school.edu").phone("010-7728-5851")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 4, 12)).department(ensureDept("SOFTWARE", "소프트웨어학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213821).name("이나래").email("20213821@school.edu").phone("010-5654-3008")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 8, 3)).department(ensureDept("MATHEMATICS", "수학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210722).name("김민재").email("20210722@school.edu").phone("010-8753-7290")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 8, 16)).department(ensureDept("INTERNATIONAL_BUSINESS", "국제경영학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213323).name("조은비").email("20213323@school.edu").phone("010-6931-1182")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 8, 16)).department(ensureDept("JAPANESE_STUDIES", "일본어문화학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212024).name("한지호").email("20212024@school.edu").phone("010-2205-4482")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 6, 19)).department(ensureDept("CHEMICAL_ENGINEERING", "화학공학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213225).name("최유진").email("20213225@school.edu").phone("010-9109-5149")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 4, 11)).department(ensureDept("ECONOMICS", "경제학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210426).name("정민호").email("20210426@school.edu").phone("010-3054-3710")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 8, 10)).department(ensureDept("ICT_ENGINEERING", "정보통신공학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213027).name("김예린").email("20213027@school.edu").phone("010-4219-6033")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 4, 25)).department(ensureDept("BIOCHEMISTRY", "생화학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212428).name("신태호").email("20212428@school.edu").phone("010-7551-2410")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 8, 16)).department(ensureDept("MARKETING", "마케팅학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213529).name("이지수").email("20213529@school.edu").phone("010-3368-7748")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 5, 10)).department(ensureDept("LIBRARY_INFORMATION", "문헌정보학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211530).name("정도현").email("20211530@school.edu").phone("010-5294-6002")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 6, 23)).department(ensureDept("SOCIOLOGY", "사회학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212531).name("김채원").email("20212531@school.edu").phone("010-7555-2209")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 4, 27)).department(ensureDept("MUSIC", "음악학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211632).name("권지후").email("20211632@school.edu").phone("010-2485-7650")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 7, 18)).department(ensureDept("CIVIL_ENGINEERING", "토목공학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20214033).name("백유정").email("20214033@school.edu").phone("010-5077-1116")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 4, 2)).department(ensureDept("ASTRONOMY", "천문학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212134).name("김성우").email("20212134@school.edu").phone("010-9993-8347")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 7, 8)).department(ensureDept("MIS", "경영정보학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210535).name("이다은").email("20210535@school.edu").phone("010-6835-4299")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 5, 14)).department(ensureDept("GERMAN_LANG", "독일어학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211836).name("조현성").email("20211836@school.edu").phone("010-3046-7418")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 7, 1)).department(ensureDept("ELECTRICAL_ENGINEERING", "전기공학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211937).name("양지인").email("20211937@school.edu").phone("010-5820-9543")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 5, 21)).department(ensureDept("THEATRE_FILM", "연극영화학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211338).name("김도현").email("20211338@school.edu").phone("010-8821-5274")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 8, 14)).department(ensureDept("GEOLOGY", "지질학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20214139).name("박하늘").email("20214139@school.edu").phone("010-7315-6025")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 7, 11)).department(ensureDept("MEDIA_COMM", "언론정보학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212640).name("정원재").email("20212640@school.edu").phone("010-4262-3339")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 7, 2)).department(ensureDept("ENVIRONMENTAL_ENGINEERING", "환경공학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213441).name("이수정").email("20213441@school.edu").phone("010-5664-4051")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 8, 25)).department(ensureDept("ACCOUNTING", "회계학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20212242).name("송지호").email("20212242@school.edu").phone("010-6258-9892")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 6, 7)).department(ensureDept("PUBLIC_ADMIN", "행정학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211043).name("박수진").email("20211043@school.edu").phone("010-3128-6900")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 7, 6)).department(ensureDept("FRENCH_LANG", "불어불문학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20214544).name("남정우").email("20214544@school.edu").phone("010-9759-5213")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 7, 13)).department(ensureDept("COMPUTER_ENGINEERING", "컴퓨터공학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20214345).name("문슬기").email("20214345@school.edu").phone("010-4445-7020")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 8, 14)).department(ensureDept("STATISTICS", "통계학과")).grade(3).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210646).name("이현우").email("20210646@school.edu").phone("010-6074-8499")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 6, 5)).department(ensureDept("MECHATRONICS", "메카트로닉스공학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211447).name("최다혜").email("20211447@school.edu").phone("010-2632-7414")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2025, 6, 23)).department(ensureDept("PAINTING", "회화학과")).grade(1).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213248).name("하민석").email("20213248@school.edu").phone("010-8899-3905")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2022, 7, 28)).department(ensureDept("ECONOMICS", "경제학과")).grade(4).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20211149).name("강유진").email("20211149@school.edu").phone("010-3117-5176")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2024, 5, 15)).department(ensureDept("CHINESE_LANG", "중국어학과")).grade(2).build());
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20210150).name("노태경").email("20210150@school.edu").phone("010-7441-6833")
                .password(passwordEncoder.encode(("student123"))).birthDate(LocalDate.of(2023, 7, 1)).department(ensureDept("ARCHITECTURE", "건축학과")).grade(3).build());

        log.info("✅ User 초기 데이터 60건이 생성되었습니다.");

        // 상담사 엔티티 생성
        Counselor counselorEntity1 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser1.getId()).counselingField(CounselingField.PSYCHOLOGICAL)
                .specialization("심리상담 전문").isActive(true).build());

        Counselor counselorEntity2 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser2.getId()).counselingField(CounselingField.CAREER)
                .specialization("진로 및 취업상담 전문").isActive(true).build());

        Counselor counselorEntity3 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser3.getId()).counselingField(CounselingField.PSYCHOLOGICAL)
                .specialization("심리상담 전문").isActive(true).build());

        Counselor counselorEntity4 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser4.getId()).counselingField(CounselingField.CAREER)
                .specialization("진로상담 전문").isActive(true).build());

        Counselor counselorEntity5 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser5.getId()).counselingField(CounselingField.EMPLOYMENT)
                .specialization("취업상담 전문").isActive(true).build());

        Counselor counselorEntity6 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser6.getId()).counselingField(CounselingField.ACADEMIC)
                .specialization("학업상담 전문").isActive(true).build());

        Counselor counselorEntity7 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser7.getId()).counselingField(CounselingField.PSYCHOLOGICAL)
                .specialization("심리상담 전문").isActive(true).build());

        Counselor counselorEntity8 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser8.getId()).counselingField(CounselingField.CAREER)
                .specialization("진로상담 전문").isActive(true).build());

        Counselor counselorEntity9 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser9.getId()).counselingField(CounselingField.EMPLOYMENT)
                .specialization("취업상담 전문").isActive(true).build());

        Counselor counselorEntity10 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser10.getId()).counselingField(CounselingField.ACADEMIC)
                .specialization("학업상담 전문").isActive(true).build());

        Counselor counselorEntity11 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser11.getId()).counselingField(CounselingField.PSYCHOLOGICAL)
                .specialization("심리상담 전문").isActive(true).build());

        Counselor counselorEntity12 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser12.getId()).counselingField(CounselingField.CAREER)
                .specialization("진로 및 취업상담 전문").isActive(true).build());

        Counselor counselorEntity13 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser13.getId()).counselingField(CounselingField.EMPLOYMENT)
                .specialization("취업상담 전문").isActive(true).build());

        Counselor counselorEntity14 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser14.getId()).counselingField(CounselingField.EMPLOYMENT)
                .specialization("취업상담 전문").isActive(true).build());

        Counselor counselorEntity15 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser15.getId()).counselingField(CounselingField.EMPLOYMENT)
                .specialization("취업상담 전문").isActive(true).build());

        Counselor counselorEntity16 = counselorRepository.save(Counselor.builder()
                .counselorId(counselorUser16.getId()).counselingField(CounselingField.EMPLOYMENT)
                .specialization("취업상담 전문").isActive(true).build());

        // 취업상담 서브필드 할당
        CounselingSubField empGeneral = counselingSubFieldRepository.findAll().stream()
                .filter(sf -> sf.getCounselingField() == CounselingField.EMPLOYMENT && sf.getSubfieldName().equals("일반 서류면접"))
                .findFirst().orElseThrow();
        CounselingSubField empForeign = counselingSubFieldRepository.findAll().stream()
                .filter(sf -> sf.getCounselingField() == CounselingField.EMPLOYMENT && sf.getSubfieldName().equals("외국계"))
                .findFirst().orElseThrow();
        CounselingSubField empEngineering = counselingSubFieldRepository.findAll().stream()
                .filter(sf -> sf.getCounselingField() == CounselingField.EMPLOYMENT && sf.getSubfieldName().equals("이공계"))
                .findFirst().orElseThrow();
        CounselingSubField empPublic = counselingSubFieldRepository.findAll().stream()
                .filter(sf -> sf.getCounselingField() == CounselingField.EMPLOYMENT && sf.getSubfieldName().equals("공기업"))
                .findFirst().orElseThrow();
        CounselingSubField empContent = counselingSubFieldRepository.findAll().stream()
                .filter(sf -> sf.getCounselingField() == CounselingField.EMPLOYMENT && sf.getSubfieldName().equals("콘텐츠엔터"))
                .findFirst().orElseThrow();
        CounselingSubField empExecutive = counselingSubFieldRepository.findAll().stream()
                .filter(sf -> sf.getCounselingField() == CounselingField.EMPLOYMENT && sf.getSubfieldName().equals("임원면접"))
                .findFirst().orElseThrow();
        
        counselorEntity5.getSpecializations().add(empGeneral);
        counselorEntity9.getSpecializations().add(empEngineering);
        counselorEntity13.getSpecializations().add(empForeign);
        counselorEntity14.getSpecializations().add(empContent);
        counselorEntity15.getSpecializations().add(empPublic);
        counselorEntity16.getSpecializations().add(empExecutive);
        counselorRepository.save(counselorEntity5);
        counselorRepository.save(counselorEntity9);
        counselorRepository.save(counselorEntity13);
        counselorRepository.save(counselorEntity14);
        counselorRepository.save(counselorEntity15);
        counselorRepository.save(counselorEntity16);

        log.info("✅ Counselor 초기 데이터 16건이 생성되었습니다.");

        // 상담사별 기본 스케줄 생성 (월-금)
        List<User> counselorUsers = List.of(
            counselorUser1, counselorUser2, counselorUser3, counselorUser4,
            counselorUser5, counselorUser6, counselorUser7, counselorUser8,
            counselorUser9, counselorUser10, counselorUser11, counselorUser12,
            counselorUser13, counselorUser14, counselorUser15, counselorUser16
        );
        
        List<User> employmentCounselors = List.of(counselorUser5, counselorUser9, counselorUser13, counselorUser14, counselorUser15, counselorUser16);
        
        for (User counselor : counselorUsers) {
            boolean isEmploymentCounselor = employmentCounselors.contains(counselor);
            for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                CounselingBaseSchedule schedule = new CounselingBaseSchedule();
                schedule.setCounselor(counselor);
                schedule.setDayOfWeek(day);
                if (isEmploymentCounselor) {
                    schedule.setSlot0910(true);
                    schedule.setSlot1011(false);
                    schedule.setSlot1112(false);
                    schedule.setSlot1213(false);
                    schedule.setSlot1314(false);
                    schedule.setSlot1415(true);
                    schedule.setSlot1516(false);
                    schedule.setSlot1617(false);
                    schedule.setSlot1718(false);
                } else {
                    schedule.setSlot0910(true);
                    schedule.setSlot1011(true);
                    schedule.setSlot1112(true);
                    schedule.setSlot1213(false);
                    schedule.setSlot1314(true);
                    schedule.setSlot1415(true);
                    schedule.setSlot1516(true);
                    schedule.setSlot1617(true);
                    schedule.setSlot1718(false);
                }
                counselingScheduleRepository.save(schedule);
            }
        }
        
        log.info("✅ 상담사 기본 스케줄 80건이 생성되었습니다.");

        // 상담 예약 데이터 (완료된 상담)
        counselingReservationRepository.save(CounselingReservation.builder().student(getUser(20213901)).counselor(counselorUser1).counselingField(CounselingField.PSYCHOLOGICAL).subField(counselingSubFieldRepository.findAll().get(0)).reservationDate(LocalDate.of(2025, 3, 15)).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(11, 0)).requestContent("학업 스트레스 상담").status(ReservationStatus.COMPLETED).createdAt(LocalDateTime.of(2025, 3, 10, 9, 0)).confirmedAt(LocalDateTime.of(2025, 3, 10, 10, 0)).completedAt(LocalDateTime.of(2025, 3, 15, 11, 0)).build());
        counselingReservationRepository.save(CounselingReservation.builder().student(getUser(20212802)).counselor(counselorUser2).counselingField(CounselingField.CAREER).subField(counselingSubFieldRepository.findAll().get(2)).reservationDate(LocalDate.of(2025, 3, 20)).startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(15, 0)).requestContent("진로 고민 상담").status(ReservationStatus.COMPLETED).createdAt(LocalDateTime.of(2025, 3, 15, 9, 0)).confirmedAt(LocalDateTime.of(2025, 3, 15, 10, 0)).completedAt(LocalDateTime.of(2025, 3, 20, 15, 0)).build());
        counselingReservationRepository.save(CounselingReservation.builder().student(getUser(20214503)).counselor(counselorUser1).counselingField(CounselingField.PSYCHOLOGICAL).subField(counselingSubFieldRepository.findAll().get(1)).reservationDate(LocalDate.of(2025, 3, 25)).startTime(LocalTime.of(11, 0)).endTime(LocalTime.of(12, 0)).requestContent("대인관계 고민").status(ReservationStatus.COMPLETED).createdAt(LocalDateTime.of(2025, 3, 20, 9, 0)).confirmedAt(LocalDateTime.of(2025, 3, 20, 10, 0)).completedAt(LocalDateTime.of(2025, 3, 25, 12, 0)).build());

        log.info("✅ 상담 예약 초기 데이터 3건이 생성되었습니다.");

        User operator = getUser(140001);
        Program prog1 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-001").title("진로 및 취업캠프").summary("진로 및 취업 준비 캠프")
                .category(ProgramCategoryType.CAREER).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 2, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 3, 31, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 3, 10, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 3, 12, 23, 59)).maxParticipants(50).minParticipants(10).mileage(20).build());
        Program prog2 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-002").title("외국어 말하기 경진대회").summary("외국어 말하기 경진대회")
                .category(ProgramCategoryType.GLOBAL).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 2, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 3, 20, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 3, 23, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 3, 23, 23, 59)).maxParticipants(30).minParticipants(10).mileage(10).build());
        Program prog3 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-003").title("심리상담 프로그램").summary("심리상담 프로그램")
                .category(ProgramCategoryType.COUNSEL).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 30, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 2, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 4, 30, 23, 59)).maxParticipants(20).minParticipants(5).mileage(15).build());
        Program prog4 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-004").title("창업 동아리").summary("창업 동아리 활동")
                .category(ProgramCategoryType.CAREER).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 2, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 3, 15, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 3, 18, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 12, 31, 23, 59)).maxParticipants(40).minParticipants(10).mileage(25).build());
        Program prog5 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-005").title("학생 멘토링 프로그램").summary("학생 멘토링 프로그램")
                .category(ProgramCategoryType.MENTOR).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 1, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 5, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 6, 30, 23, 59)).maxParticipants(50).minParticipants(10).mileage(10).build());
        Program prog6 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-006").title("리더십 워크숍").summary("리더십 워크숍")
                .category(ProgramCategoryType.LEADERSHIP).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 10, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 15, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 4, 17, 23, 59)).maxParticipants(30).minParticipants(10).mileage(20).build());
        Program prog7 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-007").title("글로벌버디").summary("글로벌버디 프로그램")
                .category(ProgramCategoryType.GLOBAL).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 2, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 3, 25, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 3, 30, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 12, 31, 23, 59)).maxParticipants(50).minParticipants(10).mileage(15).build());
        Program prog8 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-008").title("공인어학시험 대비과정").summary("공인어학시험 대비과정")
                .category(ProgramCategoryType.GLOBAL).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 5, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 8, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 6, 30, 23, 59)).maxParticipants(40).minParticipants(10).mileage(10).build());
        Program prog9 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-009").title("디지털 리터러시 특강").summary("디지털 리터러시 특강")
                .category(ProgramCategoryType.ACADEMIC).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 10, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 13, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 4, 13, 23, 59)).maxParticipants(50).minParticipants(10).mileage(10).build());
        Program prog10 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-010").title("멘토링 프로그램").summary("멘토링 프로그램")
                .category(ProgramCategoryType.MENTOR).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 3, 28, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 1, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 6, 30, 23, 59)).maxParticipants(50).minParticipants(10).mileage(10).build());
        Program prog11 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-011").title("사회봉사활동").summary("사회봉사활동")
                .category(ProgramCategoryType.VOL).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 10, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 12, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 4, 12, 23, 59)).maxParticipants(100).minParticipants(10).mileage(20).build());
        Program prog12 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-012").title("융합 캡스톤디자인").summary("융합 캡스톤디자인")
                .category(ProgramCategoryType.ACADEMIC).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 15, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 17, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 6, 30, 23, 59)).maxParticipants(30).minParticipants(10).mileage(25).build());
        Program prog13 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-013").title("공모전·경진대회").summary("공모전·경진대회")
                .category(ProgramCategoryType.CAREER).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 2, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 3, 20, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 3, 25, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 3, 25, 23, 59)).maxParticipants(50).minParticipants(10).mileage(20).build());
        Program prog14 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-014").title("교수학습 튜터링").summary("교수학습 튜터링")
                .category(ProgramCategoryType.ACADEMIC).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 2, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 3, 25, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 3, 27, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 6, 30, 23, 59)).maxParticipants(40).minParticipants(10).mileage(15).build());
        Program prog15 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-015").title("산학연계 현장실습").summary("산학연계 현장실습")
                .category(ProgramCategoryType.CAREER).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 1, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 3, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 6, 30, 23, 59)).maxParticipants(30).minParticipants(5).mileage(30).build());
        Program prog16 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-016").title("AI·SW 아카데미").summary("AI·SW 아카데미")
                .category(ProgramCategoryType.ACADEMIC).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 8, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 10, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 6, 30, 23, 59)).maxParticipants(40).minParticipants(10).mileage(20).build());
        Program prog17 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-017").title("독서감상문 대회").summary("독서감상문 대회")
                .category(ProgramCategoryType.CULTURE).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 12, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 14, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 4, 14, 23, 59)).maxParticipants(50).minParticipants(10).mileage(10).build());
        Program prog18 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-018").title("자기소개서·이력서 컨설팅").summary("자기소개서·이력서 컨설팅")
                .category(ProgramCategoryType.CAREER).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 4, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 6, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 4, 6, 23, 59)).maxParticipants(30).minParticipants(5).mileage(10).build());
        Program prog19 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-019").title("자기주도학습 특강").summary("자기주도학습 특강")
                .category(ProgramCategoryType.ACADEMIC).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 7, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 9, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 4, 9, 23, 59)).maxParticipants(50).minParticipants(10).mileage(10).build());
        Program prog20 = programRepository.save(Program.builder().owner(operator).code("PROG-2025-020").title("실전면접 클리닉").summary("실전면접 클리닉")
                .category(ProgramCategoryType.CAREER).organizerUserId(operator.getId()).status(ProgramStatus.PUBLISHED)
                .recruitStartAt(LocalDateTime.of(2025, 3, 1, 0, 0))
                .recruitEndAt(LocalDateTime.of(2025, 4, 9, 23, 59))
                .programStartAt(LocalDateTime.of(2025, 4, 11, 0, 0))
                .programEndAt(LocalDateTime.of(2025, 4, 11, 23, 59)).maxParticipants(30).minParticipants(5).mileage(15).build());

        log.info("✅ Program 초기 데이터 20건이 생성되었습니다.");

        mileageRecordRepository.save(MileageRecord.builder().student(getUser(20212802)).program(prog2).type(MileageType.EARN)
                .reason(MileageReason.PROGRAM_COMPLETION).points(10).remarks("외국어 말하기 경진대회 이수").build());
        mileageRecordRepository.save(MileageRecord.builder().student(getUser(20214503)).program(prog3).type(MileageType.EARN)
                .reason(MileageReason.PROGRAM_COMPLETION).points(15).remarks("심리상담 프로그램 이수").build());
        mileageRecordRepository.save(MileageRecord.builder().student(getUser(20214405)).program(prog5).type(MileageType.EARN)
                .reason(MileageReason.PROGRAM_COMPLETION).points(10).remarks("학생 멘토링 프로그램 이수").build());
        mileageRecordRepository.save(MileageRecord.builder().student(getUser(20213414)).program(prog14).type(MileageType.EARN)
                .reason(MileageReason.PROGRAM_COMPLETION).points(15).remarks("교수학습 튜터링 이수").build());
        mileageRecordRepository.save(MileageRecord.builder().student(getUser(20211215)).program(prog15).type(MileageType.EARN)
                .reason(MileageReason.PROGRAM_COMPLETION).points(30).remarks("산학연계 현장실습 이수").build());
        mileageRecordRepository.save(MileageRecord.builder().student(getUser(20212318)).program(prog18).type(MileageType.EARN)
                .reason(MileageReason.PROGRAM_COMPLETION).points(10).remarks("자기소개서·이력서 컨설팅 이수").build());

        log.info("✅ MileageRecord 초기 데이터 6건이 생성되었습니다.");

        // 역량 검사 섹션 생성
        AssessmentSection section1 = assessmentSectionRepository.save(AssessmentSection.builder()
                .title("자기관리 역량 검사").description("자기관리 능력 평가").isActive(true).build());
        AssessmentSection section2 = assessmentSectionRepository.save(AssessmentSection.builder()
                .title("의사소통 역량 검사").description("의사소통 능력 평가").isActive(true).build());
        AssessmentSection section3 = assessmentSectionRepository.save(AssessmentSection.builder()
                .title("글로벌 역량 검사").description("글로벌 능력 평가").isActive(true).build());
        AssessmentSection section4 = assessmentSectionRepository.save(AssessmentSection.builder()
                .title("대인관계 역량 검사").description("대인관계 능력 평가").isActive(true).build());
        AssessmentSection section5 = assessmentSectionRepository.save(AssessmentSection.builder()
                .title("종합적 사고력 검사").description("종합적 사고력 평가").isActive(true).build());
        AssessmentSection section6 = assessmentSectionRepository.save(AssessmentSection.builder()
                .title("자원·정보·기술 활용 역량 검사").description("자원·정보·기술 활용 능력 평가").isActive(true).build());

        log.info("✅ AssessmentSection 초기 데이터 6건이 생성되었습니다.");

        // 프로그램 신청 데이터 추가
        programApplicationRepository.save(ProgramApplication.builder()
                .program(prog1).student(getUser(20213901)).status(ApplicationStatus.APPROVED)
                .appliedAt(LocalDateTime.now().minusDays(10)).build());
        programApplicationRepository.save(ProgramApplication.builder()
                .program(prog2).student(getUser(20212802)).status(ApplicationStatus.APPROVED)
                .appliedAt(LocalDateTime.now().minusDays(8)).build());
        programApplicationRepository.save(ProgramApplication.builder()
                .program(prog3).student(getUser(20214503)).status(ApplicationStatus.APPROVED)
                .appliedAt(LocalDateTime.now().minusDays(5)).build());
        programApplicationRepository.save(ProgramApplication.builder()
                .program(prog16).student(getUser(20214405)).status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now().minusDays(3)).build());
        programApplicationRepository.save(ProgramApplication.builder()
                .program(prog17).student(getUser(20212206)).status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now().minusDays(2)).build());
        programApplicationRepository.save(ProgramApplication.builder()
                .program(prog18).student(getUser(20211707)).status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now().minusDays(1)).build());

        // 프로그램 현재 참여자 수 업데이트 (직접 필드 접근)
        prog1.setCurrentParticipants(15);
        prog2.setCurrentParticipants(8);
        prog3.setCurrentParticipants(12);
        prog16.setCurrentParticipants(25);
        prog17.setCurrentParticipants(18);
        prog18.setCurrentParticipants(10);
        programRepository.saveAll(List.of(prog1, prog2, prog3, prog16, prog17, prog18));

        log.info("✅ ProgramApplication 초기 데이터 6건이 생성되었습니다.");

        // 역량 데이터 추가
        createCompetencyData();
    }

    private void createCompetencyData() {
        // 자기관리 역량 (C01)
        com.competency.scms.domain.competency.Competency c01 = competencyRepository.save(
            com.competency.scms.domain.competency.Competency.builder()
                .name("자기관리 역량").compCode("C01").displayOrder(1).isActive(true).build());
        
        // 의사소통 역량 (C02)
        com.competency.scms.domain.competency.Competency c02 = competencyRepository.save(
            com.competency.scms.domain.competency.Competency.builder()
                .name("의사소통 역량").compCode("C02").displayOrder(2).isActive(true).build());
        
        // 글로벌 역량 (C03)
        com.competency.scms.domain.competency.Competency c03 = competencyRepository.save(
            com.competency.scms.domain.competency.Competency.builder()
                .name("글로벌 역량").compCode("C03").displayOrder(3).isActive(true).build());
        
        // 대인관계 역량 (C04)
        com.competency.scms.domain.competency.Competency c04 = competencyRepository.save(
            com.competency.scms.domain.competency.Competency.builder()
                .name("대인관계 역량").compCode("C04").displayOrder(4).isActive(true).build());
        
        // 종합적 사고력 (C05)
        com.competency.scms.domain.competency.Competency c05 = competencyRepository.save(
            com.competency.scms.domain.competency.Competency.builder()
                .name("종합적 사고력").compCode("C05").displayOrder(5).isActive(true).build());
        
        // 자원·정보·기술 활용 역량 (C06)
        com.competency.scms.domain.competency.Competency c06 = competencyRepository.save(
            com.competency.scms.domain.competency.Competency.builder()
                .name("자원·정보·기술 활용 역량").compCode("C06").displayOrder(6).isActive(true).build());

        log.info("✅ Competency 초기 데이터 6건이 생성되었습니다.");
    }

    private Department ensureDept(String code, String name) {
    return departmentRepository.findByCode(code)
            .orElseGet(() -> departmentRepository.save(
                    Department.builder().code(code).name(name).build()));
    }
}