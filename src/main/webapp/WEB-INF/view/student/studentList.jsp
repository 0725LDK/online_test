<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title> </title>
</head>
<body>
<!-- teacherMenu include -->
	<div>
		<c:import url="/WEB-INF/view/employee/inc/empMenu.jsp"></c:import>
	</div>
	
	<h1>Student List</h1>
	<a href="${pageContext.request.contextPath }/student/addStudent">학생등록</a>
	<table>
		<tr>
			<td>학생 ID</td>
			<td>학생 이름</td>
			<td>삭제</td>
		</tr>
		<c:forEach var="s" items="${list }">
		
			<tr>
				<td>${s.studentId }</td>
				<td>${s.studentName }</td>
				<td>
					<a href="${pageContext.request.contextPath }/student/removeStudent?studentNo=${s.studentNo}">삭제</a>
				</td>
			</tr>	
		</c:forEach>
	</table>
	
	<form action="${pageContext.request.contextPath }/student/studentList" method="get">
		<input type="text" name="searchWord">
		<button type="submit">이름 검색</button>
	</form>
	
	<div>
		<c:if test="${currentPage == 1 }">
			<span>처음으로</span>
		</c:if>
		<c:if test="${currentPage != 1 }">
			<a href="${pageContext.request.contextPath }/student/studentList?currentPage=1&searchWord=${searchWord}">처음으로</a>
		</c:if>
		<c:if test="${startPage <= 10}">
			<span>이전 10페이지</span>
		</c:if>
		<c:if test="${startPage > 1}">
			<a href="${pageContext.request.contextPath }/student/studentList?currentPage=${startPage-10}&searchWord=${searchWord}">이전 10페이지</a>
		</c:if>
			
		<c:forEach var="i" begin="${startPage }" end="${endPage }" step="1">
			<a href="${pageContext.request.contextPath }/student/studentList?currentPage=${i}">${i}</a>
		</c:forEach>
			
		<c:if test="${endPage - startPage < 9 }">
			<span>다음 10페이지</span>
		</c:if>
		<c:if test="${currentPage==lastPage}">
			<span>끝으로</span>
		</c:if>
		<c:if test="${endPage - startPage == 9 }">
			<a href="${pageContext.request.contextPath }/student/studentList?currentPage=${startPage+10}&searchWord=${searchWord}">다음 10페이지</a>
		</c:if>		
		<c:if test="${currentPage < lastPage }">
			<a href="${pageContext.request.contextPath }/student/studentList?currentPage=${lastPage}&searchWord=${searchWord}">끝으로</a>
		</c:if>
	</div>
</body>
</html>