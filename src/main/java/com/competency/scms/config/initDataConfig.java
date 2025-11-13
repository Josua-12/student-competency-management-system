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
    private final ProgramRepository programRepository;
    private final MileageRecordRepository mileageRecordRepository;
    private final SatisfactionQuestionRepository satisfactionQuestionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final ProgramApplicationRepository programApplicationRepository;
    private final AssessmentSectionRepository assessmentSectionRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;


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

        // 시스템 관리자 3명
        userRepository.save(User.builder().role(UserRole.ADMIN).userNum(100001).name("이현우").email("leehyunwoo@pureum.ac.kr").phone("010-2363-9792")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1965, 7, 27)).department(deptSysAdmin).build());
        userRepository.save(User.builder().role(UserRole.ADMIN).userNum(100002).name("임예린").email("limyerin@pureum.ac.kr").phone("010-2390-6079")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1983, 5, 24)).department(deptSysAdmin).build());
        userRepository.save(User.builder().role(UserRole.ADMIN).userNum(100003).name("조은우").email("choeunwoo@pureum.ac.kr").phone("010-9926-4095")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1968, 11, 10)).department(deptSysAdmin).build());

        // 비교과프로그램 관리자 1명
        userRepository.save(User.builder().role(UserRole.ADMIN).userNum(110001).name("박태현").email("parktaehyun@pureum.ac.kr").phone("010-4205-3849")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1961, 3, 15)).department(deptNcpAdmin).build());

        // 비교과프로그램 운영자 1명
        userRepository.save(User.builder().role(UserRole.OPERATOR).userNum(140001).name("임도윤").email("limdoyoon@pureum.ac.kr").phone("010-7136-5442")
                .password(passwordEncoder.encode(("operator123"))).birthDate(LocalDate.of(1972, 2, 13)).department(deptNcpOperator).build());

        // 상담 관리자 1명
        userRepository.save(User.builder().role(UserRole.ADMIN).userNum(120001).name("윤지훈").email("yoonjihun@pureum.ac.kr").phone("010-7316-9474")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1965, 12, 18)).department(deptCounselAdmin).build());

        // 역량관리 관리자 1명
        userRepository.save(User.builder().role(UserRole.ADMIN).userNum(130001).name("강지윤").email("kangjiyun@pureum.ac.kr").phone("010-3399-5747")
                .password(passwordEncoder.encode(("admin123"))).birthDate(LocalDate.of(1971, 12, 4)).department(deptCompetencyAdmin).build());

        // 상담 서브필드 5개
        counselingSubFieldRepository.save(CounselingSubField.builder()
                .counselingField(CounselingField.PSYCHOLOGICAL).subfieldName("우울/불안").description("우울증 및 불안장애 관련 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder()
                .counselingField(CounselingField.PSYCHOLOGICAL).subfieldName("대인관계").description("대인관계 문제 상담").build());
        counselingSubFieldRepository.save(CounselingSubField.builder()
                .counselingField(CounselingField.CAREER).subfieldName("진로탐색").description("진로 방향 설정 및 탐색").build());
        counselingSubFieldRepository.save(CounselingSubField.builder()
                .counselingField(CounselingField.EMPLOYMENT).subfieldName("이력서/자소서").description("이력서 및 자기소개서 작성 지도").build());
        counselingSubFieldRepository.save(CounselingSubField.builder()
                .counselingField(CounselingField.ACADEMIC).subfieldName("학습전략").description("효과적인 학습 방법 및 전략").build());

        log.info("✅ 상담 서브필드 초기 데이터 5건이 생성되었습니다.");

        // 상담사 12명 (학생상담센터)
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150001).name("정준호").email("jungjoonho@pureum.ac.kr").phone("010-3191-1123")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1983, 11, 14)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150002).name("조수빈").email("chosubin@pureum.ac.kr").phone("010-9053-2777")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1968, 12, 5)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150003).name("강준호").email("kangjoonho@pureum.ac.kr").phone("010-8022-6241")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1988, 5, 19)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150004).name("강현우").email("kanghyunwoo@pureum.ac.kr").phone("010-2701-1701")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1989, 4, 24)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150005).name("최하은").email("choihaeun@pureum.ac.kr").phone("010-3882-5110")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1984, 4, 19)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150006).name("임서연").email("limseoyeon@pureum.ac.kr").phone("010-6770-2619")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1968, 8, 3)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150007).name("박지민").email("parkjimin@pureum.ac.kr").phone("010-8274-4740")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1988, 9, 13)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150008).name("장민수").email("jangminsu@pureum.ac.kr").phone("010-7510-1526")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1974, 11, 17)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150009).name("김지연").email("kimjiyeon@pureum.ac.kr").phone("010-3820-1250")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1971, 9, 3)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150010).name("정유진").email("jungyujin@pureum.ac.kr").phone("010-8174-9986")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1975, 9, 16)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150011).name("최예린").email("choiyerin@pureum.ac.kr").phone("010-5069-1842")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1970, 10, 10)).department(deptCounselCenter).build());
        userRepository.save(User.builder().role(UserRole.COUNSELOR).userNum(150012).name("김민수").email("kimminsu@pureum.ac.kr").phone("010-7556-2469")
                .password(passwordEncoder.encode(("counselor123"))).birthDate(LocalDate.of(1984, 9, 16)).department(deptCounselCenter).build());

        // 학생 데이터 50명
        userRepository.save(User.builder().role(UserRole.STUDENT).userNum(20213901).name("김서윤").email("20213901@school.edu").phone("010-2958-4213")
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

        log.info("✅ User 초기 데이터 56건이 생성되었습니다.");


        Counselor counselorEntity1 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150001)).counselingField(CounselingField.PSYCHOLOGICAL)
                .specialization("심리상담 전문").isActive(true).build());

        Counselor counselorEntity2 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150002)).counselingField(CounselingField.CAREER)
                .specialization("진로 및 취업상담 전문").isActive(true).build());

        Counselor counselorEntity3 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150003)).counselingField(CounselingField.PSYCHOLOGICAL)
                .specialization("심리상담 전문").isActive(true).build());

        Counselor counselorEntity4 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150004)).counselingField(CounselingField.CAREER)
                .specialization("진로상담 전문").isActive(true).build());

        Counselor counselorEntity5 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150005)).counselingField(CounselingField.EMPLOYMENT)
                .specialization("취업상담 전문").isActive(true).build());

        Counselor counselorEntity6 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150006)).counselingField(CounselingField.ACADEMIC)
                .specialization("학업상담 전문").isActive(true).build());

        Counselor counselorEntity7 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150007)).counselingField(CounselingField.PSYCHOLOGICAL)
                .specialization("심리상담 전문").isActive(true).build());

        Counselor counselorEntity8 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150008)).counselingField(CounselingField.CAREER)
                .specialization("진로상담 전문").isActive(true).build());

        Counselor counselorEntity9 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150009)).counselingField(CounselingField.EMPLOYMENT)
                .specialization("취업상담 전문").isActive(true).build());

        Counselor counselorEntity10 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150010)).counselingField(CounselingField.ACADEMIC)
                .specialization("학업상담 전문").isActive(true).build());

        Counselor counselorEntity11 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150011)).counselingField(CounselingField.PSYCHOLOGICAL)
                .specialization("심리상담 전문").isActive(true).build());

        Counselor counselorEntity12 = counselorRepository.save(Counselor.builder()
                .counselorId(getUserId(150012)).counselingField(CounselingField.CAREER)
                .specialization("진로 및 취업상담 전문").isActive(true).build());

        log.info("✅ Counselor 초기 데이터 12건이 생성되었습니다.");

        // 상담 예약 데이터 (완료된 상담)
        counselingReservationRepository.save(CounselingReservation.builder().student(getUser(20213901)).counselor(getUser(150001)).counselingField(CounselingField.PSYCHOLOGICAL).subField(counselingSubFieldRepository.findAll().get(0)).reservationDate(LocalDate.of(2025, 3, 15)).startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(11, 0)).requestContent("학업 스트레스 상담").status(ReservationStatus.COMPLETED).createdAt(LocalDateTime.of(2025, 3, 10, 9, 0)).confirmedAt(LocalDateTime.of(2025, 3, 10, 10, 0)).completedAt(LocalDateTime.of(2025, 3, 15, 11, 0)).build());
        counselingReservationRepository.save(CounselingReservation.builder().student(getUser(20212802)).counselor(getUser(150002)).counselingField(CounselingField.CAREER).subField(counselingSubFieldRepository.findAll().get(2)).reservationDate(LocalDate.of(2025, 3, 20)).startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(15, 0)).requestContent("진로 고민 상담").status(ReservationStatus.COMPLETED).createdAt(LocalDateTime.of(2025, 3, 15, 9, 0)).confirmedAt(LocalDateTime.of(2025, 3, 15, 10, 0)).completedAt(LocalDateTime.of(2025, 3, 20, 15, 0)).build());
        counselingReservationRepository.save(CounselingReservation.builder().student(getUser(20214503)).counselor(getUser(150001)).counselingField(CounselingField.PSYCHOLOGICAL).subField(counselingSubFieldRepository.findAll().get(1)).reservationDate(LocalDate.of(2025, 3, 25)).startTime(LocalTime.of(11, 0)).endTime(LocalTime.of(12, 0)).requestContent("대인관계 고민").status(ReservationStatus.COMPLETED).createdAt(LocalDateTime.of(2025, 3, 20, 9, 0)).confirmedAt(LocalDateTime.of(2025, 3, 20, 10, 0)).completedAt(LocalDateTime.of(2025, 3, 25, 12, 0)).build());

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

        // 프로그램 참여 데이터 20건
        programApplicationRepository.save(ProgramApplication.builder().program(prog1).student(getUser(20213901)).status(ApplicationStatus.WAITLISTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 3, 10, 10, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog2).student(getUser(20212802)).status(ApplicationStatus.APPROVED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 3, 23, 10, 0)).approvedAt(LocalDateTime.of(2025, 3, 23, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog3).student(getUser(20214503)).status(ApplicationStatus.APPROVED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 2, 10, 0)).approvedAt(LocalDateTime.of(2025, 4, 2, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog4).student(getUser(20212904)).status(ApplicationStatus.REJECTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 3, 18, 10, 0)).rejectedAt(LocalDateTime.of(2025, 3, 18, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog5).student(getUser(20214405)).status(ApplicationStatus.APPROVED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 5, 10, 0)).approvedAt(LocalDateTime.of(2025, 4, 5, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog6).student(getUser(20212206)).status(ApplicationStatus.WAITLISTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 15, 10, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog7).student(getUser(20211707)).status(ApplicationStatus.REJECTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 3, 30, 10, 0)).rejectedAt(LocalDateTime.of(2025, 3, 30, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog8).student(getUser(20210808)).status(ApplicationStatus.WAITLISTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 8, 10, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog9).student(getUser(20213109)).status(ApplicationStatus.CANCELED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 13, 10, 0)).cancelledAt(LocalDateTime.of(2025, 4, 13, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog10).student(getUser(20213710)).status(ApplicationStatus.REJECTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 1, 10, 0)).rejectedAt(LocalDateTime.of(2025, 4, 1, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog11).student(getUser(20212711)).status(ApplicationStatus.WAITLISTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 12, 10, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog12).student(getUser(20210912)).status(ApplicationStatus.CANCELED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 17, 10, 0)).cancelledAt(LocalDateTime.of(2025, 4, 17, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog13).student(getUser(20210213)).status(ApplicationStatus.WAITLISTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 3, 25, 10, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog14).student(getUser(20213414)).status(ApplicationStatus.APPROVED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 3, 27, 10, 0)).approvedAt(LocalDateTime.of(2025, 3, 27, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog15).student(getUser(20211215)).status(ApplicationStatus.APPROVED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 3, 10, 0)).approvedAt(LocalDateTime.of(2025, 4, 3, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog16).student(getUser(20214216)).status(ApplicationStatus.WAITLISTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 10, 10, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog17).student(getUser(20214617)).status(ApplicationStatus.WAITLISTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 14, 10, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog18).student(getUser(20212318)).status(ApplicationStatus.APPROVED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 6, 10, 0)).approvedAt(LocalDateTime.of(2025, 4, 6, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog19).student(getUser(20213619)).status(ApplicationStatus.REJECTED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 9, 10, 0)).rejectedAt(LocalDateTime.of(2025, 4, 9, 11, 0)).build());
        programApplicationRepository.save(ProgramApplication.builder().program(prog20).student(getUser(20210320)).status(ApplicationStatus.CANCELED).consentYn(true)
                .appliedAt(LocalDateTime.of(2025, 4, 11, 10, 0)).cancelledAt(LocalDateTime.of(2025, 4, 11, 11, 0)).build());

        log.info("✅ ProgramApplication 초기 데이터 20건이 생성되었습니다.");

        // 상담 후 만족도 조사 질문 중 시스템 질문들(counselingField = null)
        SatisfactionQuestion question1 = satisfactionQuestionRepository.save(SatisfactionQuestion.builder()
                .questionText("상담사의 전문성과 태도에 만족하십니까?")
                .isSystemDefault(true)
                .questionType(SatisfactionQuestion.QuestionType.RATING)
                .displayOrder(1)
                .isRequired(true)
                .isActive(true)
                .build());

        SatisfactionQuestion question2 = satisfactionQuestionRepository.save(SatisfactionQuestion.builder()
                .questionText("상담을 통해 문제 해결에 도움을 받으셨습니까?")
                .isSystemDefault(true)
                .questionType(SatisfactionQuestion.QuestionType.RATING)
                .displayOrder(2)
                .isRequired(true)
                .isActive(true)
                .build());

        SatisfactionQuestion question3 = satisfactionQuestionRepository.save(SatisfactionQuestion.builder()
                .questionText("추가로 하고 싶은 말씀이 있으신가요?")
                .isSystemDefault(true)
                .questionType(SatisfactionQuestion.QuestionType.TEXT) //주관식
                .displayOrder(3)
                .isRequired(false)
                .isActive(true)
                .build());

        SatisfactionQuestion question4 = satisfactionQuestionRepository.save(SatisfactionQuestion.builder()
                .questionText("향후 선호하는 상담 방식은 무엇입니까?")
                .questionType(SatisfactionQuestion.QuestionType.MULTIPLE_CHOICE)
                .displayOrder(4)
                .isSystemDefault(false)
                .isRequired(false)
                .isActive(true)
                .build());

        questionOptionRepository.save(QuestionOption.builder()
                .question(question4).optionText("대면 상담").optionValue(1).displayOrder(1).build());

        questionOptionRepository.save(QuestionOption.builder()
                .question(question4).optionText("화상 상담").optionValue(2).displayOrder(2).build());

        questionOptionRepository.save(QuestionOption.builder()
                .question(question4).optionText("카톡 상담").optionValue(3).displayOrder(3).build());

        questionOptionRepository.save(QuestionOption.builder()
                .question(question4).optionText("전화 상담").optionValue(4).displayOrder(4).build());

        log.info("✅ 상담 후 만족도 조사 질문 4건(시스템 기본)이 생성되었습니다.");
        log.info("✅ QuestionOption 초기 데이터 4건이 생성되었습니다.");

        // 진단 세션(AssessmentSection) 초기화 데이터

        LocalDateTime now = LocalDateTime.now();

        // 1. [진행 중]
        AssessmentSection activeSection = AssessmentSection.builder()
                .title("2025학년도 1학기 정기 핵심역량 진단")
                .description("재학생 전체를 대상으로 하는 정기 진단입니다. 성실히 응답해 주세요.")
                .startDate(now.minusDays(7))
                .endDate(now.plusDays(7))
                .isActive(true)
                .build();

        // 2. [종료됨]
        AssessmentSection expiredSection = AssessmentSection.builder()
                .title("2024학년도 2학기 정기 핵심역량 진단")
                .description("지난 학기 진단 결과입니다.")
                .startDate(now.minusMonths(2))
                .endDate(now.minusMonths(1))
                .isActive(true)
                .build();

        // 3. [예정됨]
        AssessmentSection upcomingSection = AssessmentSection.builder()
                .title("2025학년도 여름방학 특별 진단")
                .description("여름방학 프로그램 참여자를 위한 사전 진단입니다.")
                .startDate(now.plusMonths(1))
                .endDate(now.plusMonths(2))
                .isActive(true)
                .build();

        // 4. [비활성]
        AssessmentSection inactiveSection = AssessmentSection.builder()
                .title("(임시) 관리자 테스트용 진단")
                .description("학생들에게는 보이지 않는 테스트 항목입니다.")
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .isActive(false)
                .build();

        assessmentSectionRepository.saveAll(List.of(activeSection, expiredSection, upcomingSection, inactiveSection));
        log.info("✅ AssessmentSection 초기 데이터 4건이 생성되었습니다.");

        log.info("테스트 데이터 초기화 완료");


    }

    private Department ensureDept(String code, String name) {
        return departmentRepository.findByCode(code)
                .orElseGet(() -> departmentRepository.save(
                    Department.builder().code(code).name(name).build()));
    }



}

