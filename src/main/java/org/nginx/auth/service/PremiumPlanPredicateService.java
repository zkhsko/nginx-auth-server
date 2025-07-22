package org.nginx.auth.service;

import org.nginx.auth.repository.PremiumPlanPredicateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PremiumPlanPredicateService {

    @Autowired
    private PremiumPlanPredicateRepository premiumPlanPredicateRepository;



}
