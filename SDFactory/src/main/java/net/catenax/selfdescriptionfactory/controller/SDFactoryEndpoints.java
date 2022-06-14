package net.catenax.selfdescriptionfactory.controller;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.selfdescriptionfactory.dto.SDDocumentDto;
import net.catenax.selfdescriptionfactory.service.SDFactory;
import net.catenax.selfdescriptionfactory.util.BeanAsMap;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("selfdescription")
@RequiredArgsConstructor
@Slf4j
public class SDFactoryEndpoints {

    private final SDFactory sdFactory;

    @Operation(
            method = "POST",
            description = "Creates a Verifiable Credential and saves in the DB from the DTO",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Verifiable Credential was created successfully",
                    content = @Content(
                            mediaType = "application/vc+ld+json",
                            examples = @ExampleObject("""
{
  "id": "http://sdhub.int.demo.catena-x.net/selfdescription/vc/62a22d735c6fb508ce9088c8",
  "@context": [
    "https://www.w3.org/2018/credentials/v1",
    "https://df2af0fe-d34a-4c48-abda-c9cdf5718b4a.mock.pstmn.io/sd-document-v0.1.jsonld"
  ],
  "type": [
    "VerifiableCredential",
    "SD-document"
  ],
  "issuer": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
  "issuanceDate": "2022-06-09T17:27:15Z",
  "expirationDate": "2022-09-07T17:27:15Z",
  "credentialSubject": {
    "bpn": "BPNL000000000000",
    "company_number": "123456",
    "headquarter_country": "DE",
    "legal_country": "DE",
    "sd_type": "connector",
    "service_provider": "http://test.d.com",
    "id": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh"
  },
  "proof": {
    "type": "Ed25519Signature2018",
    "created": "2022-06-09T17:27:19Z",
    "proofPurpose": "assertionMethod",
    "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
    "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..0gCJd2wVGvUM-UwVvoIRVlUUgODI5CSFkazyOQlClg4AaxhMH3pcDhvO_UxU1uo3wBU5ArxvKAil3gxOBgE1Aw"
  }
}
""" ))), @ApiResponse(responseCode = "403",
            description = "Access is forbidden",
            content = @Content(
                    mediaType = "application/vc+ld+json",
                    examples = @ExampleObject("""
{
  "timestamp": "2022-06-09T17:29:02.235+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/selfdescription"
}
"""))), @ApiResponse(responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                    mediaType = "application/vc+ld+json",
                    examples = @ExampleObject("""
{
  "timestamp": "2022-06-09T17:35:39.926+00:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/selfdescription"
}
""")))})
    @PostMapping(consumes = {"application/json"}, produces = {"application/vc+ld+json"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole(@securityRoles.createRole)")
    public VerifiableCredential publishSelfDescription(@RequestBody SDDocumentDto sdDocumentDto) throws Exception {
        var sdMap = new HashMap<>(BeanAsMap.asMap(sdDocumentDto));
        sdMap.remove("issuer");
        sdMap.remove("holder");
        sdMap.values().removeAll(Collections.singleton(null));
        var id = UUID.randomUUID();
        var verifiedCredentials = sdFactory.createVC(id.toString(), sdMap,
                sdDocumentDto.getHolder(), sdDocumentDto.getIssuer());
        sdFactory.storeVC(verifiedCredentials, sdDocumentDto, id);
        return verifiedCredentials;
    }

    @Operation(
            method = "DELETE",
            description = "Delete a list of Verifiable Credentials from DB",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Verifiable Credentials were deleted successfully"),
            @ApiResponse(responseCode = "403",
                    description = "Access is forbidden",
                    content = @Content(
                            mediaType = "application/vc+ld+json",
                            examples = @ExampleObject("""
{
  "timestamp": "2022-06-09T17:29:02.235+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/selfdescription"
}
"""))), @ApiResponse(responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                    mediaType = "application/vc+ld+json",
                    examples = @ExampleObject("""
{
  "timestamp": "2022-06-09T17:35:39.926+00:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/selfdescription"
}
""")))})
    @DeleteMapping
    @PreAuthorize("hasRole(@securityRoles.deleteRole)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSelfDescriptions(@RequestParam(value = "id", required = true) List<String> ids) {
        sdFactory.removeSelfDescriptions(ids);
    }
}
