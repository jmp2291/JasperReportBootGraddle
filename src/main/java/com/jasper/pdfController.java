package com.jasper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * pdfController Class
 * 
 * Controller for View PDF from jrxml template .
 *  
 * @author yusuke
 * @Version 1.0
 * @since 2018/1/24
 * 
 */
@RestController
public class pdfController {
	
	@Autowired
    ApplicationContext context;
	
	private static final Logger LOG = LoggerFactory.getLogger(pdfController.class);
    	
	/**
     * getPdf method
     * 
     * Get JRXML Template name From PathVarialbe and View PDF.
     * 
     * It's include Database connection.
     * 
     * ConnectionString is sensitive privacy.
     * So, Don't upload github.
     * 
     * @param jrxml Template Name. not {@code null}
     * @param name Parameter for jrxml template.
     * @param response HttpServletResponse need for export PDF Stream.
     * @throws IOException jrxml template not found
     * @throws JRException jrxml complie error
     *  
     */    
	@GetMapping(path = "pdf/{jrxml}")
	@ResponseBody
    public void getPdf(@PathVariable String jrxml,@RequestParam("no") Optional<Integer> no,@RequestParam("date") Optional<Date> date, HttpServletResponse response) throws IOException, JRException, ClassNotFoundException, SQLException{
		Resource resource = context.getResource("classpath:jasperreports/"+jrxml+".jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();
        LOG.info("InputStream [{}]", inputStream);
        
        JasperReport report=JasperCompileManager.compileReport(inputStream);		
        
        LOG.info("Report [{}]", report);

        //Parameters Set
        Map<String, Object> params = new HashMap<>();
        Deadline deadline=new Deadline();
        params=deadline.getParameter();
        params.put("no", no.orElse(null));
        params.put("date", date.orElse(null));
        
        //LOG
        LOG.info("Template is [{}]. params is [{}] date is [{}]", jrxml,params,deadline.nowdate);

        
        //Data source Set
        JRDataSource dataSource = new JREmptyDataSource();
        //Make jasperPrint
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
        //Media Type
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        //Export PDF Stream
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}