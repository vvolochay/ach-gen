package com.vvolochay

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileReader
import java.util.*

data class Color(
    val name: String,
    val hex: String,
    val fontColor: String
)

class GenUtils {
    companion object {

        fun parseColorLibrary(colors: File): Map<String, Color> {
            val reader = JsonReader(FileReader(colors))
            val list: Array<Color> = Gson().fromJson(reader, Array<Color>::class.java)

            return list.associateBy { it.name }
        }

        fun base64Logo(logo: File, id: Int, defaultLogo: File): String {
            var logoPath = defaultLogo
            if (logo.isDirectory) {
                val path = File(logo.path + "/" + id + "-logo.png")
                if (path.exists()) {
                    logoPath = path
                } else {
                    println("LOGO MISSED $id")
                }
            }

            val fileContent: ByteArray = FileUtils.readFileToByteArray(logoPath)
            return Base64.getEncoder().encodeToString(fileContent)
        }

        /**
         * fix non-supported symbols ascii -> html
         */
        fun replaceEscapingSymbols(str: String): String {
            return str.replace("&", "&amp;").replace("<", "&#60;")
        }

        fun placeRating(rating: String, x: Int, r: String): String {
            val ratingCircle = "<g id=\"Frame 1\">\n" +
                    "<g id=\"Rating circle\">\n" +
                    "        <rect x=\"{X_coord}\" y=\"92.9756\" width=\"31.0488\" height=\"29.0488\" rx=\"14.5244\"\n" +
                    "     fill=\"{fontColor}\" fill-opacity=\"0.8\"/>\n" +
                    "        <svg x=\"{X_coord}\" y=\"92.9756\" width=\"31.0488\" height=\"30.0488\">\n" +
                    "        <text id=\"rate\" fill=\"{mainColor}\" xml:space=\"preserve\" style=\"white-space: pre\"\n" +
                    "     font-family=\"Helvetica\" x=\"50%\" y=\"50%\"\n" +
                    "alignment-baseline=\"middle\" text-anchor=\"middle\" font-size=\"11.2341\" font-weight=\"800\"\n" +
                    "   letter-spacing=\"0em\">{R}</text>\n" +
                    "       </svg>\n" +
                    "       </g>\n" +
                    "        <text id=\"Rating\" fill=\"{fontColor}\" fill-opacity=\"0.8\" xml:space=\"preserve\"\n" +
                    "    style=\"white-space: pre\" font-family=\"Helvetica\" font-size=\"16.8512\"\n" +
                    "     font-weight=\"800\" letter-spacing=\"0em\"><tspan x=\"{X_coord_2}\" y=\"113.398\">{Rating}</tspan>\n" +
                    "       </text>\n" +
                    " </g>"

            return ratingCircle.replace("{Rating}", rating)
                .replace("{X_coord_2}", (x + 39).toString())
                .replace("{X_coord}", x.toString())
                .replace("{R}", r)
        }

        fun addMedals(replaced: String, university: University): String {
            val medalsPattern = " <g id=\"Frame 148\">\n" +
                    "     <g id=\"Frame 206\">\n" +
                    "         <rect x=\"279\" y=\"19\" width=\"147.12\" height=\"105.892\" rx=\"14.5856\" fill=\"{fontColor}\"\n" +
                    "               fill-opacity=\"0.2\"/>\n" +
                    "         <g id=\"Frame 205\">\n" +
                    "             <g id=\"Frame 201\">\n" +
                    "                 <text id=\"&#195;&#151; 5\" opacity=\"0.75\" fill=\"white\" xml:space=\"preserve\"\n" +
                    "                       style=\"white-space: pre\" font-family=\"Helvetica\" font-size=\"15.12\"\n" +
                    "                       letter-spacing=\"0em\"><tspan x=\"282.197\" y=\"49.7878\">&#xd7; G</tspan></text>\n" +
                    "                 <g id=\"Group 37\">\n" +
                    "                     <path id=\"Vector\" fill-rule=\"evenodd\" clip-rule=\"evenodd\"\n" +
                    "                           d=\"M294.657 96.113C296.691 96.113 298.499 96.9076 299.855 98.2697C301.211 99.6317 302.002 101.448 302.002 103.491C302.002 105.534 301.211 107.35 299.855 108.712C298.499 110.074 296.691 110.869 294.657 110.869C292.51 110.869 290.702 110.074 289.346 108.712C288.103 107.35 287.199 105.534 287.199 103.491C287.199 101.448 288.103 99.6317 289.346 98.2697C290.702 96.9076 292.623 96.113 294.657 96.113ZM294.657 90.8916C298.047 90.8916 301.211 92.2536 303.471 94.524C305.844 96.7942 307.2 99.9724 307.2 103.492C307.2 107.01 305.844 110.189 303.471 112.459C301.211 114.729 298.047 116.092 294.657 116.092C291.154 116.092 287.989 114.73 285.729 112.459C283.469 110.189 282 107.011 282 103.492C282 99.9729 283.469 96.7944 285.729 94.524C288.102 92.1405 291.266 90.8916 294.657 90.8916ZM294.657 95.2049C292.284 95.2049 290.25 96.113 288.78 97.5885C287.312 99.1778 286.407 101.221 286.407 103.491C286.407 105.761 287.312 107.804 288.78 109.394C290.249 110.869 292.283 111.777 294.657 111.777C296.917 111.777 298.951 110.869 300.42 109.394C301.889 107.804 302.906 105.761 302.906 103.491C302.906 101.221 301.889 99.1778 300.42 97.5885C298.951 96.113 296.917 95.2049 294.657 95.2049Z\"\n" +
                    "                           fill=\"url(#paint0_linear_1307_20980)\"/>\n" +
                    "                     <g id=\"Vector_2\" opacity=\"0.5\">\n" +
                    "                         <path d=\"M282.676 78.2007L289.117 91.1247L289.12 91.1236C290.023 90.7748 290.926 90.4258 292.055 90.1932L294.089 86.0016L290.134 78.2007H282.676Z\"\n" +
                    "                               fill=\"white\"/>\n" +
                    "                         <path d=\"M299.061 78.2002L293.072 90.0767C293.524 89.9603 294.089 89.9599 294.654 89.9599C296.575 89.9599 298.496 90.4255 300.191 91.1242L306.632 78.2002H299.061Z\"\n" +
                    "                               fill=\"white\"/>\n" +
                    "                     </g>\n" +
                    "                 </g>\n" +
                    "             </g>\n" +
                    "             <g id=\"Frame 203\">\n" +
                    "                 <text id=\"&#195;&#151; 6\" opacity=\"0.75\" fill=\"white\" xml:space=\"preserve\"\n" +
                    "                       style=\"white-space: pre\" font-family=\"Helvetica\" font-size=\"15.12\"\n" +
                    "                       letter-spacing=\"0em\"><tspan x=\"338.479\" y=\"49.7878\">&#xd7; S</tspan></text>\n" +
                    "                 <g id=\"Group 38\">\n" +
                    "                     <path id=\"Vector_3\" fill-rule=\"evenodd\" clip-rule=\"evenodd\"\n" +
                    "                           d=\"M350.097 96.113C352.131 96.113 353.939 96.9076 355.295 98.2697C356.651 99.6317 357.442 101.448 357.442 103.491C357.442 105.534 356.651 107.35 355.295 108.712C353.939 110.074 352.131 110.869 350.097 110.869C347.95 110.869 346.142 110.074 344.786 108.712C343.543 107.35 342.638 105.534 342.638 103.491C342.638 101.448 343.543 99.6317 344.786 98.2697C346.142 96.9076 348.063 96.113 350.097 96.113ZM350.097 90.8916C353.487 90.8916 356.651 92.2536 358.911 94.524C361.284 96.7942 362.64 99.9724 362.64 103.492C362.64 107.01 361.284 110.189 358.911 112.459C356.651 114.729 353.487 116.092 350.097 116.092C346.594 116.092 343.429 114.73 341.169 112.459C338.909 110.189 337.44 107.011 337.44 103.492C337.44 99.9729 338.909 96.7944 341.169 94.524C343.542 92.1405 346.706 90.8916 350.097 90.8916ZM350.097 95.2049C347.724 95.2049 345.69 96.113 344.22 97.5885C342.751 99.1778 341.847 101.221 341.847 103.491C341.847 105.761 342.751 107.804 344.22 109.394C345.689 110.869 347.723 111.777 350.097 111.777C352.357 111.777 354.391 110.869 355.86 109.394C357.329 107.804 358.346 105.761 358.346 103.491C358.346 101.221 357.329 99.1778 355.86 97.5885C354.391 96.113 352.357 95.2049 350.097 95.2049Z\"\n" +
                    "                           fill=\"url(#paint1_linear_1307_20980)\"/>\n" +
                    "                     <g id=\"Vector_4\" opacity=\"0.5\">\n" +
                    "                         <path d=\"M338.12 78.2007L344.561 91.1247L344.564 91.1236C345.467 90.7748 346.37 90.4258 347.499 90.1932L349.533 86.0016L345.578 78.2007H338.12Z\"\n" +
                    "                               fill=\"white\"/>\n" +
                    "                         <path d=\"M354.505 78.2002L348.516 90.0767C348.968 89.9603 349.533 89.9599 350.098 89.9599C352.019 89.9599 353.94 90.4255 355.635 91.1242L362.076 78.2002H354.505Z\"\n" +
                    "                               fill=\"white\"/>\n" +
                    "                     </g>\n" +
                    "                 </g>\n" +
                    "             </g>\n" +
                    "             <g id=\"Frame 204\">\n" +
                    "                 <text id=\"&#195;&#151; 10\" opacity=\"0.75\" fill=\"white\" xml:space=\"preserve\"\n" +
                    "                       style=\"white-space: pre\" font-family=\"Helvetica\" font-size=\"15.12\"\n" +
                    "                       letter-spacing=\"0em\"><tspan x=\"393.505\" y=\"49.7878\">&#xd7; B</tspan></text>\n" +
                    "                 <g id=\"Group 39\">\n" +
                    "                     <path id=\"Vector_5\" fill-rule=\"evenodd\" clip-rule=\"evenodd\"\n" +
                    "                           d=\"M405.537 96.113C407.571 96.113 409.379 96.9076 410.735 98.2697C412.091 99.6317 412.882 101.448 412.882 103.491C412.882 105.534 412.091 107.35 410.735 108.712C409.379 110.074 407.571 110.869 405.537 110.869C403.39 110.869 401.582 110.074 400.226 108.712C398.983 107.35 398.079 105.534 398.079 103.491C398.079 101.448 398.983 99.6317 400.226 98.2697C401.582 96.9076 403.503 96.113 405.537 96.113ZM405.537 90.8916C408.927 90.8916 412.091 92.2536 414.351 94.524C416.724 96.7942 418.08 99.9724 418.08 103.492C418.08 107.01 416.724 110.189 414.351 112.459C412.091 114.729 408.927 116.092 405.537 116.092C402.034 116.092 398.87 114.73 396.609 112.459C394.349 110.189 392.88 107.011 392.88 103.492C392.88 99.9729 394.349 96.7944 396.609 94.524C398.982 92.1405 402.146 90.8916 405.537 90.8916ZM405.537 95.2049C403.164 95.2049 401.13 96.113 399.661 97.5885C398.192 99.1778 397.288 101.221 397.288 103.491C397.288 105.761 398.192 107.804 399.661 109.394C401.13 110.869 403.164 111.777 405.537 111.777C407.797 111.777 409.831 110.869 411.3 109.394C412.769 107.804 413.786 105.761 413.786 103.491C413.786 101.221 412.769 99.1778 411.3 97.5885C409.831 96.113 407.797 95.2049 405.537 95.2049Z\"\n" +
                    "                           fill=\"url(#paint2_linear_1307_20980)\"/>\n" +
                    "                     <g id=\"Vector_6\" opacity=\"0.5\">\n" +
                    "                         <path d=\"M393.56 78.2007L400.001 91.1247L400.004 91.1236C400.907 90.7748 401.81 90.4258 402.939 90.1932L404.973 86.0016L401.018 78.2007H393.56Z\"\n" +
                    "                               fill=\"white\"/>\n" +
                    "                         <path d=\"M409.945 78.2002L403.956 90.0767C404.408 89.9603 404.973 89.9599 405.538 89.9599C407.458 89.9599 409.38 90.4255 411.075 91.1242L417.516 78.2002H409.945Z\"\n" +
                    "                               fill=\"white\"/>\n" +
                    "                     </g>\n" +
                    "                 </g>\n" +
                    "             </g>\n" +
                    "         </g>\n" +
                    "     </g>\n" +
                    " </g>\n"

            val gold = university.goldYears?.size ?: 0
            val silver = university.silverYears?.size ?: 0
            val bronze = university.bronzeYears?.size ?: 0

            if (gold == 0 && silver == 0 && bronze == 0) return replaced

            return replaced
                .replace("{Medals}", medalsPattern)
                .replace("&#xd7; G", "&#xd7; $gold")
                .replace("&#xd7; S", "&#xd7; $silver")
                .replace("&#xd7; B", "&#xd7; $bronze")

        }
        fun addCups(replaced: String, university: University, lastX: Int): String {
            val cup = "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M29.3598 20.8657C27.9118 20.7595 25.0148 19.5284 25.0148 18H74.9798C74.9798 19.5284 72.0838 20.7595 70.6348 20.8657H29.3598ZM30.6228 52.07L28.7008 22.1394H71.1398L69.2188 52.07C69.2188 57.164 66.9298 63.851 59.0458 63.851C57.4628 63.851 57.4818 65.175 57.5168 67.66C57.5228 68.059 57.5288 68.488 57.5288 68.945V70.168C62.0028 72.73 66.0138 76.63 66.0138 82H33.9838C33.9838 76.692 37.9018 72.821 42.3118 70.258V68.945C42.3118 68.488 42.3178 68.059 42.3238 67.66C42.3598 65.175 42.3778 63.851 40.7948 63.851C32.9118 63.851 30.6228 57.164 30.6228 52.07ZM21.8478 37.15C22.2358 42.886 25.8908 46.622 28.2258 47.695C28.5988 47.866 28.8908 48.198 28.9358 48.606L29.3768 52.551C29.4428 53.143 28.9708 53.648 28.4078 53.451C24.7078 52.162 18.4928 45.118 18.0018 37.149C17.9678 36.598 18.4178 36.149 18.9708 36.149H20.8148C21.3668 36.149 21.8108 36.599 21.8478 37.15ZM78.1518 37.15C77.7648 42.886 74.1088 46.622 71.7748 47.695C71.4018 47.866 71.1088 48.198 71.0638 48.606L70.6228 52.551C70.5568 53.143 71.0288 53.648 71.5918 53.451C75.2928 52.162 81.5078 45.118 81.9978 37.149C82.0318 36.598 81.5818 36.149 81.0298 36.149H79.1858C78.6328 36.149 78.1898 36.599 78.1518 37.15Z\" fill=\"url(#paint0_linear_5796_14962)\"/>\n" +
                    "<defs>\n" +
                    "<linearGradient id=\"paint0_linear_5796_14962\" x1=\"17.9998\" y1=\"18\" x2=\"81.9998\" y2=\"18\" gradientUnits=\"userSpaceOnUse\">\n" +
                    "<stop stop-color=\"white\"/>\n" +
                    "<stop offset=\"0.180056\" stop-color=\"#C9C9C9\"/>\n" +
                    "<stop offset=\"0.368853\" stop-color=\"white\"/>\n" +
                    "<stop offset=\"0.483125\" stop-color=\"#CDCDCD\"/>\n" +
                    "<stop offset=\"0.706701\" stop-color=\"#B5B5B5\"/>\n" +
                    "<stop offset=\"1\" stop-color=\"#F5F5F5\"/>\n" +
                    "</linearGradient>\n" +
                    "</defs>"
            var allCups = ""
            if (university.winYears != null) {
                for (year in university.winYears) {
                    val newCup = cup
                        .replace("{year}", year.toString())
                        .replace("{xCoord}", (lastX + 10).toString())
                    allCups += newCup
                }
            }

            return replaced.replace("{AllCups}", allCups)
        }

        fun addRC(replaced: String, university: University, lastX: Int): String {
            var lastXCoord = lastX;
            val rc = "<g id=\"Frame 144\">\n" +
                    "    <text id=\"{year}\" opacity=\"0.8\" fill=\"{fontColor}\" xml:space=\"preserve\" style=\"white-space: pre\"\n" +
                    "          font-family=\"Helvetica\" font-size=\"14\" letter-spacing=\"0em\"><tspan x=\"470.62\" y=\"41.248\">{year}</tspan></text>\n" +
                    "    <g id=\"Frame 179\">\n" +
                    "        <rect x=\"{xCoord}\" y=\"48\" width=\"75\" height=\"44\" rx=\"6.66667\"\n" +
                    "              fill=\"url(#paint3_linear_1307_20980)\"/>\n" +
                    "        <text id=\"Regional Champion\" fill=\"#464646\" xml:space=\"preserve\" style=\"white-space: pre\"\n" +
                    "              font-family=\"Helvetica\" font-size=\"10\" letter-spacing=\"0em\">REGIONAL&#10;\n" +
                    "            CHAMPION</text>\n" +
                    "    </g>\n" +
                    "</g>"

            var allRC = ""
            if (university.winYears != null) {
                for (year in university.winYears) {
                    lastXCoord += 10
                    val newRC = rc
                        .replace("{year}", year.toString())
                        .replace("{xCoord}", (lastXCoord + 10).toString())
                    allRC += newRC

                }
            }

            return replaced.replace("{AllRC}", allRC)

        }
    }
}