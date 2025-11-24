package br.com.furb.rotasegura.infra.scheduler;

import br.com.furb.rotasegura.services.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    private SchedulerService schedulerService;

    @EventListener(ApplicationEvent.class)
    public void onInit() {
        schedulerService.createRoles();
        schedulerService.createMasterUser();

    }
}
