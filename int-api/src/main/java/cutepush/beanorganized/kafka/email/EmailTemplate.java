package cutepush.beanorganized.kafka.email;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class EmailTemplate {

    public static final String HEADER_IMAGE = "https://i.imgur.com/vpeWVH1.png";

    public static String headerRow() {
        return "    <tr>" +
                "      <td style='background:#EA1D2C;padding:18px 20px;color:#ffffff;display:flex;align-items:center;'>" +
                "        <img src='" + HEADER_IMAGE + "' alt='Bean Organized' style='height:48px;margin-right:12px;border-radius:6px;'/>" +
                "        <div style='line-height:1'>" +
                "          <h1 style='margin:0;font-size:20px;line-height:1.1'>Bean Organized</h1>" +
                "          <p style='margin:6px 0 0;font-size:13px;opacity:.95'>Sua organização no ponto certo 😉</p>" +
                "        </div>" +
                "      </td>" +
                "    </tr>\n";
    }

    public static String footerRow() {
        return "    <tr>" +
                "      <td style='background:#fafafa;color:#888;font-size:12px;padding:16px 24px;text-align:center'>" +
                "        © " + java.time.Year.now() + " Bean Organized — Feito com ❤️ no Brasil" +
                "      </td>" +
                "    </tr>\n";
    }

    /**
     * Envolve as linhas internas (innerRows) com o wrapper completo do email.
     * innerRows deve conter um ou mais <tr>...</tr> que compõem o corpo da mensagem.
     */
    public static String wrapWithShell(String innerRows) {
        return "<div style='font-family:Arial,Helvetica,sans-serif;background:#f6f6f6;padding:24px;'>" +
                "  <table role='presentation' cellpadding='0' cellspacing='0' width='100%' style='max-width:640px;margin:0 auto;background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 8px 24px rgba(0,0,0,0.08)'>\n" +
                headerRow() +
                innerRows +
                footerRow() +
                "  </table>" +
                "</div>";
    }
}

