package com.technicaleval.transfers.dto.api;

import java.util.List;

public record RecipientsGetResponse(
        List<RecipientData> recipient
) {
}
