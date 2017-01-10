package com.itcast.cookie;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
 * ! JDK不支持,采用#号替代
 */
public class CookieDemo1 extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		//1.显示商品详情
		String id=req.getParameter("id");
		Book book=(Book) DB.getAll().get(id);
		PrintWriter out=resp.getWriter();
		
		out.write(book.getName()+"<br/>");
		out.write(book.getAuthor()+"<br/>");
		out.write(book.getDescription()+"<br/>");
		
		
		//2.回写Cookie
		String cookieValue=buildCookie(id,req);
		Cookie cookie=new Cookie("history", cookieValue);
		cookie.setMaxAge(1000*3600);
		cookie.setPath("/ShowViewRecords");
		resp.addCookie(cookie);
	}

	private String buildCookie(String id, HttpServletRequest req) {
		//	当前情况		   		新增记录		更新记录
		//a.浏览记录为null      		1			1
		//b.浏览记录[ 2 ,4 ,1 ] 		1		[ 1 ,2 ,4 ]
		//c.浏览记录[ 2 ,4 ,3 ]		1		[ 1 ,2 ,4 ]
		//d.浏览记录[ 2 ,4 ]			1		[ 1 ,2 ,4 ]
		String bookHistory=null;
		Cookie[] cookies=req.getCookies();
		if (cookies!=null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("history")) {
					bookHistory=cookie.getValue();
					System.out.println(bookHistory);
				}
			}
		}
		//a
		if (bookHistory==null) {
			return id;
		}
		LinkedList<String> list=new LinkedList<String>(Arrays.asList(bookHistory.split("\\#")));
		//b
		if (list.contains(id)) {
			list.remove(id);
//			list.addFirst(id);
		}else{
			//c
			if (list.size()>=3) {
				list.removeLast();
//				list.addFirst(id);
			}
//			else{//d
////				list.addFirst(id);
//			}
		}
		
		//优化后
		list.addFirst(id);
		StringBuffer sb=new StringBuffer();
		for (String bid : list) {
			sb.append(bid+"#");
		}
		return sb.deleteCharAt(sb.length()-1).toString();
	}
}
