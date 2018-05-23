package com.exp.demo.task;

import org.springframework.stereotype.*;
import com.exp.demo.service.*;
import org.springframework.beans.factory.annotation.*;
import com.exp.demo.vo.*;
import org.springframework.scheduling.annotation.*;

@Service
public class SdTask
{
    @Autowired
    private SdService sdService;

    @Scheduled(cron = "0 0/10 12,13,14,15,16,17,18,19,20,21,22,23 * * ? ")
    public void task() {
        final XczyVo xczyVo = new XczyVo();
        xczyVo.setMainAccount("18302319069");
        xczyVo.setPayPassword("962492");
        xczyVo.setZhiAccount("17132566435,17132566489,17132566495,17132566540,17132566624,17132566714,17132566748,17132566754,17158016240,13724099268,13430203242,13802782538,13556033011,13729377889,13632240741,13710629296,13609029005,13694242214,16620071685");
        xczyVo.setLoginPassword("asd47760011");
        this.sdService.shouge(xczyVo);
        this.sdService.delivery(xczyVo);
        this.sdService.finish(xczyVo);
    }
}
