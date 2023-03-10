package goodee.gdj58.online.controller;

import java.util.List;	
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import goodee.gdj58.online.service.StudentService;
import goodee.gdj58.online.vo.Paper;
import goodee.gdj58.online.vo.Score;
import goodee.gdj58.online.vo.Student;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class StudentController {
	@Autowired StudentService studentService;
	
	//pw 수정 폼
	@GetMapping("/student/studentModifyPw")
	public String modifyStudentPw()
	{
		
		return "student/studentModifyPw";
	}
	//pw 수정 액션
	@PostMapping("/student/studentModifyPw")
	public String modifyStudentPw(HttpSession session, @RequestParam(value = "oldPw") String oldPw, @RequestParam(value = "newPw") String newPw)
	{
		Student loginStudent = (Student)session.getAttribute("loginStudent");
		
		int row = studentService.updateStudentPw(loginStudent.getStudentNo() , oldPw, newPw);
		if(row==0)
		{
			return "student/studentModifyPw";
		}
		return "redirect:/student/studentTestList";
	}
	
	/*
	 * //학생 시험 점수
	 * 
	 * @GetMapping("/student/studentTestPaper") public String
	 * studentTestPaper(HttpSession session, Model model ,@RequestParam(value =
	 * "studentNo") int studentNo ,@RequestParam(value = "testNo") int testNo ) {
	 * log.debug("학생시험점수 컨트롤러"); int testScore =
	 * studentService.studentCalScore(studentNo); Score score = new Score();
	 * score.setTestNo(testNo); score.setStudentNo(studentNo);
	 * score.setScore(testScore); studentService.studentAddScore(score);
	 * 
	 * model.addAttribute("score",score); return
	 * "/student/studentTestList?studentNo="+studentNo; }
	 */
	//학생 시험 점수 계산 및 저장
	@GetMapping("student/studentCalScore")
	public String studentCalScore(HttpSession session, Model model
									,@RequestParam(value = "studentNo") int studentNo
									,@RequestParam(value = "testNo") int testNo
									)
	{
		
		int testScore = studentService.studentCalScore(studentNo,testNo);
		
		Score score = new Score();
		score.setTestNo(testNo);
		score.setStudentNo(studentNo);
		score.setScore(testScore);
		
		studentService.studentAddScore(score);
		log.debug("학생 시험 점수 계산 및 저장 컨트롤러");
		
		return"redirect:/student/studentTestList";
	}
	
	
	
	//학생 문제 제출 답안
	@PostMapping("/student/studentTestPaper")
	public String studentTestPaper(HttpSession session, Model model, Paper paper
									,@RequestParam(value = "choice1", defaultValue = "5") int answer1 
									,@RequestParam(value = "choice2", defaultValue = "5") int answer2 
									,@RequestParam(value = "choice3", defaultValue = "5") int answer3 
									,@RequestParam(value = "choice4", defaultValue = "5") int answer4 
									,@RequestParam(value = "choice5", defaultValue = "5") int answer5 
									,@RequestParam(value = "studentNo") int studentNo
									,@RequestParam(value = "testNo") int testNo
									,@RequestParam(value = "questionNo1",defaultValue = "") int questionNo1
									,@RequestParam(value = "questionNo2",defaultValue = "") int questionNo2
									,@RequestParam(value = "questionNo3",defaultValue = "") int questionNo3
									,@RequestParam(value = "questionNo4",defaultValue = "") int questionNo4
									,@RequestParam(value = "questionNo5",defaultValue = "") int questionNo5
									)
	{
		
		
		
		int[] answer = {answer1,answer2,answer3,answer4,answer5};
		int[] question = {questionNo1,questionNo2,questionNo3,questionNo4,questionNo5};
		int j=0;
		for (int i : answer) {
			if(i<5)
			{
				paper.setAnswer(i);
				paper.setQuestionNo(question[j]);
				paper.setStudentNo(studentNo);
				studentService.studentTestPaper(paper);
				j++;
			}
		}
		log.debug("학생제줄답안 컨트롤러");
		model.addAttribute("studentNo", studentNo);
		model.addAttribute("testNo", testNo);
		return "redirect:/student/studentCalScore?testNo="+testNo+"&studentNo="+studentNo;
	}
	
	//학생 시험별 문제 리스트
	@GetMapping("/student/studentQuestionList")
	public String questionList(HttpSession session, Model model, 
								@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
								@RequestParam(value = "rowPerPage", defaultValue = "10") int rowPerPage,
								@RequestParam(value = "searchWord", defaultValue = "") String searchWord,
								@RequestParam(value = "testNo", defaultValue = "") int testNo,
								@RequestParam(value = "studentNo", defaultValue = "") int studentNo
								)//int currentPage = Integer.parseInt(request.getParamenter("currentPage"));
	{
		
		log.debug(searchWord+"<---searchWord");
		
		int questionTotalCount = studentService.questionTotalCount(searchWord);
		int endPage = (int)(Math.ceil(currentPage / 10.0)) * 10; //페이징 버튼의 끝
		int startPage = endPage - 9; //페이징 버튼의 시작
		int firstPage = 1;
		int lastPage = (int)(Math.ceil(questionTotalCount * 1.0/rowPerPage));
		if(lastPage < endPage )
		{
			endPage = lastPage;
		}
		
		List<Map<String,Object>> list = studentService.getQuestionList(currentPage,rowPerPage,searchWord,testNo);
		List<Map<String, Object>> exampleList = studentService.getExampleList(testNo);
		//request.setAttribute("list", list);
		model.addAttribute("list",list);
		model.addAttribute("exampleList",exampleList);
		model.addAttribute("currentPage",currentPage);
		model.addAttribute("endPage",endPage);
		model.addAttribute("startPage",startPage);
		model.addAttribute("firstPage",firstPage);
		model.addAttribute("lastPage",lastPage);
		model.addAttribute("searchWord",searchWord);
		model.addAttribute("testNo",testNo);
		model.addAttribute("studentNo",studentNo);
		model.addAttribute("questionTotalCount",questionTotalCount);
		return "student/studentQuestionList";
	}

	//학생 시험 리스트
	@GetMapping("/student/studentTestList")
	public String testList(HttpSession session, Model model, 
							@RequestParam(value = "studentNo", defaultValue = "0") int studentNo
							)//int currentPage = Integer.parseInt(request.getParamenter("currentPage"));
		{
		
		Student loginStudent = (Student)session.getAttribute("loginStudent");
		if(loginStudent == null)
		{
			return "redirect:/student/loginStudent";
		}
		
		studentNo = loginStudent.getStudentNo();
		
		List<Map<String,Object>> list = studentService.getTestList(studentNo);
		List<Map<String,Object>> endList = studentService.getEndTestList(studentNo);
		//request.setAttribute("list", list);
		model.addAttribute("list",list);
		model.addAttribute("endList",endList);
		return "student/studentTestList";
	}
	
	//로그인 폼
	@GetMapping("/loginStudent")
	public String loginStudent(HttpSession session)//세션이 필요하면 매개변수로 세션을 받자
	{
		
		return "student/loginStudent";
	}
	//로그인 액션
	@PostMapping("/loginStudent")
	public String loginStudent(HttpSession session, Student student)
	{
		Student resultStudent = studentService.login(student);
		
		session.setAttribute("loginStudent", resultStudent);
		return "redirect:/student/studentTestList";
	}
	//로그아웃 액션
	@GetMapping("/student/logout")
	public String logout(HttpSession session) 
	{
		session.invalidate();
		return "redirect:/employee/loginEmp";
	}

}
