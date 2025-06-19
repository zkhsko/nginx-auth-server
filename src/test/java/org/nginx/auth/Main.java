package org.nginx.auth;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.nginx.auth.security.LicenseBasedTokenHelper;
import org.nginx.auth.util.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dongpo.li
 * @date 2023/12/19
 */
public class Main {

    public static void main(String[] args) {
        String s = LicenseBasedTokenHelper.create();
        System.out.println(s);
        String key = "QW6rJl2USbv9WghX";

        try {
            String encrypt = LicenseBasedTokenHelper.encrypt(s, key);
            System.out.println(encrypt);

            String decrypt = LicenseBasedTokenHelper.decrypt(encrypt, key);
            System.out.println(decrypt);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void mainxxx(String[] args) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 0);

            BitMatrix bitMatrix = qrCodeWriter.encode("https://qr.alipay.com/bax027268isd0nck5ymp556d", BarcodeFormat.QR_CODE, 250, 250, hints);


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

            byte[] bytes = out.toByteArray();

            byte[] encode = Base64.getEncoder().encode(bytes);

            System.out.println(new String(encode));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main1(String[] args) {

        String appId = "2021004130624254";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCz4VTGaLEH7Zf29h6/MeK0vZbqrINt3qJeGaOb7dkKsWUCy4nkcg5BLm0JLNYyL8KKxBT9yEUg2WlcbT3VJwsy3PxS596G7lqjv5T6vBVlhyPHa9rAtinOrMrd2VQYNbToL6s3uOIeTmyEFBJfJRjlCTpRKLfNkK9MtmcTc1y+TtFwljSOYDIc/9mzC3M02YSdZJU9Nv3o93QMexsqkgShoRQ/3UI2Az1GH0fC/y5Mu15JdxaLWuNfh2AkW3JnJm+EqxkLgQjjnJrNkgdFZBzavF7XgC0zHqRRpn5k3rQiLco+vVhlYZYt4SL7r0uaOxTXa1YeCmfmSoltLA6WNFfXAgMBAAECggEAGlR8bUJ7z1ZG7T5fh8sMjTpHMfiokFeLWszT9wocwATxbTRP7eGIFqBuE269t8Dn8Bn9nJxJCiLo2j+fUmkRqZNRV+1a1FNlbxym+QC4/RCH+zPdZGIFwpvmYgzi01xhK692l4R3jMob9kVd3xF2EGiGYcTdCrkH/+mfMuirBawSSQ1LrVq+sPkNGRsATKiO0Zy9/UOnUv937QakRAHK3FWIvTsbJsOC+CNbMun81kdKCjhoPNceUwZmnEtZKqYXmuMTmRn5gWFCqN24fTdT8xyj3UaIE3YcbU/kWsb6X/x3YQf971AWK66O0nKjRY34wiFkO93zNrP7ayUWBFV1qQKBgQD6Q6tPG3FBMnvhY+SjlqKS14EcIgYlx6nX2Drum7i19xY+0+lbDXmOviphRD1yp46uT9qYzn1xZDvEg+EmbLelG0FXKvteGczkCy85mfjWXni8/KNlsiZYLUd1pTN/DFPK/Uau4rpaMlHcQYmLJ0EF3Pdq/F1u4E2YSJZdpv9Q5QKBgQC4ALW30zSk3j8r1f5zZl1Hl05qPetCANTpLi2DPnEnpc7vSWzOvnguy0AXxvbl/S6ZrbQ6xpG7eKyRfoi4wEAHoVsRKCHl8ScpzMG3akGj+1q+OXsZRTAgtmOiEb8Vn5U3moFWZtGt1PO8cIIX3iRLVvFjDVYAIZOHTWaqdxSGCwKBgQDo4634WMZE8LDdZvMu1JAHbCpGvtbPs4U3So5U227BTuVRpCwlQl3bWtXfW/x+xLR/O+plhsqviaIg1sTf2A3xxHJCpsnyoLnNgSb6SHGLM7DR1ZxMl8MdDU3RieFpGlSjspxjRLQ8GVWaTRas+uOEjENmOODNvEgZLXtYPX6cjQKBgQCSCQ4wXVJU34cnlWmj0USLQ00bCRWkhuB0SRAaL/7CxtgcFhxjYcwk529w75qZIn5Pvgti8qJLNhVQTCra5spqw+dxEHcG9oRsqnO8zHpYWW/Ftf07KyxtahRdVtRfpw5Y2Sl5l5Um7/KaRnkZ1Fbd7J0cTkNJS1y5b/bhqC0DzQKBgGGL4e1wiG5lqmjSc6ywrFFGU/vmBcsKcqS6zTLsDvvEEStX3NaE1YSGDCrIBeHauWIR1/yF0ME9skeVQnoKoBkEqF6U08ZfKqADIYC/e0qBUwCa4dxz0FOi5SETAZOJ3SI8pJZ/6XVQe7NpTIFPdq5schyFi9h6hxombu19eaGg";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmg72zRVNOQI8oSw+JwcQbn3LoE8RcufcV7kzlAvfXlP3oh+S+0tqYR2Q60rX6EpROL6Ew4wEDubvZ47YbuV2vW11fIRE9S5Y3cOkjHVrHIAdZdN6INHJ2KibjlM3zPRGqYWpIKW4E/D5NukPZNnf/ZxvzON0rcqrzKTGDLTc8rglmbVtMamr9BlXUe0XBmH6+Oo+1PXy51mdO6VHe3oUk3MvS1kNfzgQHjhQyGHSUZUutFa3LlfROayhFBr/oqhvnpfVAw3IxnnXl0JxY2Gi4qfhybfu3LrjbwHjxdSu3Kkyerl1kEUOuYtRkK4LpmVshxMpyy2u9biK/tzJSrxzBQIDAQAB";

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",appId,privateKey,"json","UTF-8", alipayPublicKey,"RSA2");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl("");
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", "202312201853160000000051969");
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "测试商品");


        request.setBizContent(JsonUtils.toJson(bizContent));
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);

            System.out.println(JsonUtils.toJson(response));
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }


}
