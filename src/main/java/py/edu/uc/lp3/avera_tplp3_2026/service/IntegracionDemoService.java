package py.edu.uc.lp3.avera_tplp3_2026.service;

import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.IntegracionDemoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.IntegracionDemoResponse;

public interface IntegracionDemoService {

    IntegracionDemoResponse ejecutar(IntegracionDemoRequest request);
}
