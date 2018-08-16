package com.sunrun.controller;

import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.Org;
import com.sunrun.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("org")
public class OrgController {
    @Autowired
    private OrgService orgService;

    @GetMapping("domain/{domainId}")
    public ReturnData getOrgList(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                                 @PathVariable(name = "domainId")Integer domainId,
                                 @RequestParam(name = "parentId",defaultValue = "0")Long parentId){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        List<Org> data = null;
        try {
            data = orgService.findByDomainIdAndParentId(domainId,parentId);
            noticeMessage = NoticeMessage.SUCCESS;
        } catch (Exception e){
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }
}
