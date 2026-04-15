package com.erp.rrhh.servicio;

import static com.erp.rrhh.servicio.RrhhSupport.divide;
import static com.erp.rrhh.servicio.RrhhSupport.safe;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.rrhh.dto.DashboardResponse;
import com.erp.rrhh.modelo.RrhhBenefit;
import com.erp.rrhh.modelo.RrhhCandidate;
import com.erp.rrhh.modelo.RrhhClimateSurvey;
import com.erp.rrhh.modelo.RrhhConflictCase;
import com.erp.rrhh.modelo.RrhhEmployee;
import com.erp.rrhh.modelo.RrhhEmployeeLeave;
import com.erp.rrhh.modelo.RrhhOnboarding;
import com.erp.rrhh.modelo.RrhhPayroll;
import com.erp.rrhh.modelo.RrhhPerformanceReview;
import com.erp.rrhh.modelo.RrhhTrainingPlan;
import com.erp.rrhh.modelo.RrhhVacancy;
import com.erp.rrhh.modelo.RrhhWellbeingProgram;
import com.erp.rrhh.repositorio.RrhhBenefitRepository;
import com.erp.rrhh.repositorio.RrhhCandidateRepository;
import com.erp.rrhh.repositorio.RrhhClimateSurveyRepository;
import com.erp.rrhh.repositorio.RrhhConflictCaseRepository;
import com.erp.rrhh.repositorio.RrhhEmployeeLeaveRepository;
import com.erp.rrhh.repositorio.RrhhEmployeeRepository;
import com.erp.rrhh.repositorio.RrhhOnboardingRepository;
import com.erp.rrhh.repositorio.RrhhPayrollRepository;
import com.erp.rrhh.repositorio.RrhhPerformanceReviewRepository;
import com.erp.rrhh.repositorio.RrhhTrainingPlanRepository;
import com.erp.rrhh.repositorio.RrhhVacancyRepository;
import com.erp.rrhh.repositorio.RrhhWellbeingProgramRepository;
import com.erp.rrhh.spec.RrhhSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RrhhDashboardService {

    private final RrhhEmployeeRepository employeeRepository;
    private final RrhhEmployeeLeaveRepository leaveRepository;
    private final RrhhVacancyRepository vacancyRepository;
    private final RrhhCandidateRepository candidateRepository;
    private final RrhhOnboardingRepository onboardingRepository;
    private final RrhhPerformanceReviewRepository reviewRepository;
    private final RrhhTrainingPlanRepository trainingRepository;
    private final RrhhPayrollRepository payrollRepository;
    private final RrhhBenefitRepository benefitRepository;
    private final RrhhClimateSurveyRepository climateRepository;
    private final RrhhWellbeingProgramRepository wellbeingRepository;
    private final RrhhConflictCaseRepository conflictRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String area, String contractType, LocalDate fromDate, LocalDate toDate,
            String dependency) {
        LocalDate effectiveFrom = fromDate == null ? LocalDate.now().minusDays(30) : fromDate;
        LocalDate effectiveTo = toDate == null ? LocalDate.now() : toDate;

        List<RrhhEmployee> employees = employeeRepository.findAll(employeeSpec(area, contractType, dependency));
        List<RrhhEmployee> activeEmployees = employees.stream().filter(e -> Boolean.TRUE.equals(e.getActive())).toList();
        List<RrhhEmployee> recentHires = employees.stream()
                .filter(e -> between(e.getHireDate(), effectiveFrom, effectiveTo))
                .toList();
        List<RrhhEmployee> recentTerminations = employees.stream()
                .filter(e -> between(e.getTerminationDate(), effectiveFrom, effectiveTo))
                .toList();

        List<RrhhEmployeeLeave> leaves = leaveRepository.findAll().stream()
                .filter(l -> matchEmployee(l.getEmployee(), area, contractType, dependency))
                .filter(l -> between(l.getStartDate(), effectiveFrom, effectiveTo))
                .toList();
        BigDecimal leaveDays = leaves.stream().map(RrhhEmployeeLeave::getDays).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal absenteeismRate = divide(leaveDays.multiply(BigDecimal.valueOf(100)),
                BigDecimal.valueOf(Math.max(1, activeEmployees.size() * 30L)));

        List<RrhhVacancy> vacancies = vacancyRepository.findAll(vacancySpec(area, dependency)).stream()
                .filter(v -> between(v.getOpenDate(), effectiveFrom, effectiveTo) || "OPEN".equalsIgnoreCase(v.getStatus()))
                .toList();
        long openVacancies = vacancies.stream().filter(v -> "OPEN".equalsIgnoreCase(v.getStatus())).count();

        List<RrhhOnboarding> onboarding = onboardingRepository.findAll().stream()
                .filter(o -> o.getCandidate() != null && o.getCandidate().getVacancy() != null)
                .filter(o -> between(o.getStartDate(), effectiveFrom, effectiveTo))
                .filter(o -> matchVacancy(o.getCandidate().getVacancy(), area, dependency))
                .toList();
        BigDecimal averageHiringDays = onboarding.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(onboarding.stream()
                        .mapToLong(o -> ChronoUnit.DAYS.between(o.getCandidate().getVacancy().getOpenDate(), o.getStartDate()))
                        .average().orElse(0d))
                        .setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal hiringCost = vacancies.stream()
                .map(RrhhVacancy::getBudgetedSalary)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<RrhhPerformanceReview> reviews = reviewRepository.findAll().stream()
                .filter(r -> matchEmployee(r.getEmployee(), area, contractType, dependency))
                .filter(r -> between(r.getReviewDate(), effectiveFrom, effectiveTo))
                .toList();
        BigDecimal avgReview = reviews.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(reviews.stream().map(RrhhPerformanceReview::getScore).mapToDouble(BigDecimal::doubleValue).average().orElse(0d))
                        .setScale(2, java.math.RoundingMode.HALF_UP);

        List<RrhhTrainingPlan> trainings = trainingRepository.findAll().stream()
                .filter(t -> area == null || area.isBlank() || area.equalsIgnoreCase(t.getArea()))
                .filter(t -> between(t.getStartDate(), effectiveFrom, effectiveTo))
                .toList();
        BigDecimal trainingHours = trainings.stream().map(RrhhTrainingPlan::getHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        long trainedEmployees = trainings.stream().map(t -> t.getEmployee() == null ? null : t.getEmployee().getId()).filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet()).size();
        BigDecimal trainedPercentage = divide(BigDecimal.valueOf(trainedEmployees * 100L),
                BigDecimal.valueOf(Math.max(1, activeEmployees.size())));

        List<RrhhPayroll> payrolls = payrollRepository.findAll().stream()
                .filter(p -> matchEmployee(p.getEmployee(), area, contractType, dependency))
                .filter(p -> between(p.getPeriodStart(), effectiveFrom, effectiveTo))
                .toList();
        BigDecimal monthlyPayrollCost = payrolls.stream().map(RrhhPayroll::getNetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal overtimeHours = payrolls.stream().map(RrhhPayroll::getOvertimeHours).reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeBenefits = benefitRepository.findAll().stream()
                .filter(b -> matchEmployee(b.getEmployee(), area, contractType, dependency))
                .filter(b -> "ACTIVE".equalsIgnoreCase(b.getStatus()))
                .count();

        BigDecimal jobSatisfaction = climateRepository.findAll().stream()
                .filter(c -> area == null || area.isBlank() || area.equalsIgnoreCase(c.getArea()))
                .filter(c -> between(c.getStartDate(), effectiveFrom, effectiveTo))
                .map(RrhhClimateSurvey::getSatisfactionScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<RrhhClimateSurvey> climateRows = climateRepository.findAll().stream()
                .filter(c -> area == null || area.isBlank() || area.equalsIgnoreCase(c.getArea()))
                .filter(c -> between(c.getStartDate(), effectiveFrom, effectiveTo))
                .toList();
        if (!climateRows.isEmpty()) {
            jobSatisfaction = divide(jobSatisfaction, BigDecimal.valueOf(climateRows.size()));
        }

        List<RrhhWellbeingProgram> wellbeingPrograms = wellbeingRepository.findAll().stream()
                .filter(w -> area == null || area.isBlank() || area.equalsIgnoreCase(w.getArea()))
                .filter(w -> between(w.getStartDate(), effectiveFrom, effectiveTo))
                .toList();
        BigDecimal wellbeingParticipation = wellbeingPrograms.isEmpty() ? BigDecimal.ZERO
                : divide(wellbeingPrograms.stream().map(RrhhWellbeingProgram::getParticipationRate).reduce(BigDecimal.ZERO, BigDecimal::add),
                        BigDecimal.valueOf(wellbeingPrograms.size()));

        List<RrhhConflictCase> conflicts = conflictRepository.findAll().stream()
                .filter(c -> matchEmployee(c.getEmployee(), area, contractType, dependency))
                .filter(c -> between(c.getOpenedAt(), effectiveFrom, effectiveTo))
                .toList();

        return DashboardResponse.builder()
                .activeEmployees(activeEmployees.size())
                .recentHires(recentHires.size())
                .recentTerminations(recentTerminations.size())
                .rotationRate(divide(BigDecimal.valueOf(recentTerminations.size() * 100L),
                        BigDecimal.valueOf(Math.max(1, activeEmployees.size()))))
                .absenteeismRate(absenteeismRate)
                .openVacancies(openVacancies)
                .averageHiringDays(averageHiringDays)
                .hiringCost(hiringCost)
                .averagePerformanceScore(avgReview)
                .trainingHours(trainingHours)
                .trainedPercentage(trainedPercentage)
                .monthlyPayrollCost(monthlyPayrollCost)
                .overtimeHours(overtimeHours)
                .activeBenefits(activeBenefits)
                .jobSatisfaction(safe(jobSatisfaction))
                .wellbeingParticipation(wellbeingParticipation)
                .reportedConflicts(conflicts.size())
                .resolvedConflicts(conflicts.stream().filter(c -> "RESOLVED".equalsIgnoreCase(c.getStatus())).count())
                .build();
    }

    private Specification<RrhhEmployee> employeeSpec(String area, String contractType, String dependency) {
        return Specification.<RrhhEmployee>where(RrhhSpecifications.likeIgnoreCase("area", area))
                .and(RrhhSpecifications.likeIgnoreCase("contractType", contractType))
                .and(RrhhSpecifications.likeIgnoreCase("dependency", dependency));
    }

    private Specification<RrhhVacancy> vacancySpec(String area, String dependency) {
        return Specification.<RrhhVacancy>where(RrhhSpecifications.likeIgnoreCase("area", area))
                .and(RrhhSpecifications.likeIgnoreCase("dependency", dependency));
    }

    private boolean between(LocalDate value, LocalDate from, LocalDate to) {
        return value != null && !value.isBefore(from) && !value.isAfter(to);
    }

    private boolean matchEmployee(RrhhEmployee employee, String area, String contractType, String dependency) {
        if (employee == null) {
            return true;
        }
        boolean areaOk = area == null || area.isBlank() || area.equalsIgnoreCase(employee.getArea());
        boolean contractOk = contractType == null || contractType.isBlank()
                || contractType.equalsIgnoreCase(employee.getContractType());
        boolean dependencyOk = dependency == null || dependency.isBlank()
                || dependency.equalsIgnoreCase(employee.getDependency());
        return areaOk && contractOk && dependencyOk;
    }

    private boolean matchVacancy(RrhhVacancy vacancy, String area, String dependency) {
        boolean areaOk = area == null || area.isBlank() || area.equalsIgnoreCase(vacancy.getArea());
        boolean dependencyOk = dependency == null || dependency.isBlank()
                || dependency.equalsIgnoreCase(vacancy.getDependency());
        return areaOk && dependencyOk;
    }
}

