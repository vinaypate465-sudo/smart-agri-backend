package com.smartagri.controller;

import com.smartagri.entity.Crop;
import com.smartagri.entity.Expense;
import com.smartagri.repository.CropRepository;
import com.smartagri.repository.ExpenseRepository;
import com.smartagri.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseRepository expenses;
    private final CropRepository crops;
    private final CurrentUser me;

    public ExpenseController(ExpenseRepository expenses, CropRepository crops, CurrentUser me) {
        this.expenses = expenses; this.crops = crops; this.me = me;
    }

    private Crop ownedCrop(Expense in) {
        if (in.getCrop() == null || in.getCrop().getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select a crop");
        Crop c = crops.findById(in.getCrop().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop not found"));
        if (!c.getFarm().getOwner().getId().equals(me.get().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your crop");
        return c;
    }

    private Expense owned(Long id) {
        Expense e = expenses.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        if (!e.getCrop().getFarm().getOwner().getId().equals(me.get().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your expense");
        return e;
    }

    private void validate(Expense in) {
        if (in.getAmount() == null || in.getAmount() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than 0");
    }

    @GetMapping
    public List<Expense> list() { return expenses.findByCropFarmOwnerId(me.get().getId()); }

    @PostMapping
    public Expense create(@RequestBody Expense in) {
        validate(in);
        in.setId(null);
        in.setCrop(ownedCrop(in));
        return expenses.save(in);
    }

    @PutMapping("/{id}")
    public Expense update(@PathVariable Long id, @RequestBody Expense in) {
        validate(in);
        Expense e = owned(id);
        e.setCrop(ownedCrop(in));
        e.setCategory(in.getCategory());
        e.setAmount(in.getAmount());
        e.setDate(in.getDate());
        e.setNote(in.getNote());
        return expenses.save(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { expenses.delete(owned(id)); }
}
