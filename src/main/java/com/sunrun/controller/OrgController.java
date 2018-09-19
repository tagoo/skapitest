package com.sunrun.controller;

import com.sunrun.common.notice.NoticeFactory;
import com.sunrun.common.notice.NoticeMessage;
import com.sunrun.common.notice.ReturnData;
import com.sunrun.entity.Domain;
import com.sunrun.entity.Org;
import com.sunrun.exception.NotFindDomainException;
import com.sunrun.service.DomainService;
import com.sunrun.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("org")
public class OrgController {
    @Autowired
    private OrgService orgService;

    @Autowired
    private DomainService domainService;

    @GetMapping("domain")
    public ReturnData getOrgList(@RequestParam(name = "lang", defaultValue = "zh")String lang,
                                 @RequestParam(name = "domainId",required = false)Integer domainId,
                                 @RequestParam(name = "domainName",required = false)String domainName,
                                 @RequestParam(name = "parentId",defaultValue = "0")Long parentId,
                                 @RequestParam(name = "direction",defaultValue = "asc")String direction,
                                 @RequestParam(name = "order",defaultValue = "sortNumber")String order){
        NoticeMessage noticeMessage = NoticeMessage.POST_PARAMS_ERROR;
        List<Org> data = null;
        try {
            if (null != domainId || StringUtils.hasText(domainName)) {
                if (domainId == null) {
                   domainId = domainService.findByName(domainName).getId();
                }
                data = orgService.findByDomainIdAndParentId(domainId,parentId,Sort.by(Sort.Direction.fromString(direction),order));
                noticeMessage = NoticeMessage.SUCCESS;
            }

        } catch (NotFindDomainException e){
            noticeMessage = NoticeMessage.DOMAIN_NOT_EXIST;
            e.printStackTrace();
        }
        return NoticeFactory.createNoticeWithFlag(noticeMessage, lang, data);
    }
}
