package com.sunrun.controller;

import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.Domain;
import com.sunrun.entity.TaskFile;
import com.sunrun.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("domain")
public class DomainController {
    @Autowired
    private DomainService domainService;
    @GetMapping
    public ReturnData findFiles(@RequestParam(name = "lang", defaultValue = "zh") String lang,
                                @RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                @RequestParam(name = "size",defaultValue = "15",required = false)int size){
        NoticeMessage noticeMessage = NoticeMessage.FAILED;
        Page<Domain> taskFileList = null;
        Pageable pageable =  null;
        try {
            if (page > -1) {
                pageable = PageRequest.of(page, size);
            }
            taskFileList = domainService.findAll(pageable);
            noticeMessage = NoticeMessage.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, taskFileList);
    }
}
