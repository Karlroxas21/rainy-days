package com.rainydaysengine.rainydays.interfaces.web.user;

import com.rainydaysengine.rainydays.application.port.auth.Session;

import com.rainydaysengine.rainydays.application.service.entry.DepositEntryDto;
import com.rainydaysengine.rainydays.application.service.entry.Entry;
import com.rainydaysengine.rainydays.application.service.entry.EntryResponse;
import com.rainydaysengine.rainydays.application.service.entry.RecentEntriesResponse;
import com.rainydaysengine.rainydays.application.service.pagination.PaginationResponse;
import com.rainydaysengine.rainydays.application.service.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final User user;
    private final Entry entry;

    private static final int DEFAULT_PAGE_SIZE = 5;

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<UserRegisterResponse>> registerUser(
            @Valid @RequestBody UserRequestDto requestDto) {

        return user.userRegister(requestDto)
                .thenApply(userRegisterResponse -> ResponseEntity.status(201).body(userRegisterResponse));

    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest loginRequest) {
        UserLoginResponse response = user.userLogin(loginRequest.getIdentifier(), loginRequest.getPassword());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/whoami")
    public ResponseEntity<?> whoAmI(@RequestBody UserWhoAmIRequest userWhoAmIRequest) {
        Session session = user.whoAmI(userWhoAmIRequest.getSession_token());

        return ResponseEntity.ok(session);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid UserResetPasswordRequest userResetPasswordRequest) {

        String email = userResetPasswordRequest.getIdentity();
        String password = userResetPasswordRequest.getPassword();

        user.resetPassword(email, password);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/add-entry", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addEntry(@ModelAttribute @Valid DepositEntryDto depositEntryDto) {

        String newEntry = entry.addEntry(depositEntryDto);

        return ResponseEntity.ok(newEntry);
    }

    @PostMapping(value = "/add-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addPhoto(@RequestParam("file") MultipartFile file) throws Exception {

        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }

    // param options: sort=<value>,desc|asc
    // page, size, search=value
    @GetMapping(value = "/{userId}/entries")
    public ResponseEntity<PaginationResponse<RecentEntriesResponse>> getRecentEntriesByUserId(
            @PathVariable String userId,
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0, size =  DEFAULT_PAGE_SIZE, direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        String searchValue = (search == null || search.isBlank()) ? "" : search;

        Page<RecentEntriesResponse> userEntries = entry.recentEntriesByUserId(userId, searchValue, pageable);

        PaginationResponse<RecentEntriesResponse> res = new PaginationResponse<>(
                userEntries.getContent(),
                userEntries.getNumber(),
                userEntries.getTotalElements(),
                userEntries.getTotalPages(),
                userEntries.getSize(),
                pageable.getSort().toString()
        );

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/{userId}/entries/{entryId}")
    public ResponseEntity<EntryResponse> getEntryByEntryId(
            @PathVariable String userId,
            @PathVariable String entryId,
            @PageableDefault(page = 0, size = DEFAULT_PAGE_SIZE, direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        EntryResponse userEntry = entry.findEntry(entryId, userId);

        EntryResponse res = new EntryResponse(
                userEntry.entryId(),
                userEntry.amount(),
                userEntry.notes(),
                userEntry.photoEvidence(),
                userEntry.createdAt(),
                userEntry.updatedAt(),
                userEntry.groupId(),
                userEntry.groupName()
        );

        return ResponseEntity.ok(res);
    }

}
