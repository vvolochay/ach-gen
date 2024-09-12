from bs4 import BeautifulSoup, NavigableString
from pathlib import Path


images = "_main.svg", "_contestant_0.svg", "_contestant_1.svg", "_contestant_2.svg", "_coach.svg", "_finals.svg", "_main.svg"
for i in range(137, 143):
    print(i)
    soups = []
    if not Path(f"result/{i}_main.svg").exists():
        print("skipping")
        continue
    for img in images:
        p = Path(f"result/" + str(i) + img)
        if p.exists():
            with open(p) as f:
                content = f.readlines()
                content = "".join(content)
                soups.append(BeautifulSoup(content, "xml"))
    screentime = 5
    duration = 0.5

    out = soups[0].svg
    new_children = []

    css = soups[0].new_tag('style')

    css_string = """
            @keyframes turn-off {
                        0% { opacity: 1; }
                        100% { opacity: 0; }
            }
    """

    sum_prev = 0
    for index, soup in enumerate(soups):
        svg = soup.svg
        tag = soups[0].new_tag('g', **{'class': f'scene{index}', 'transform': 'translate(0, 836)'})
        if index > 0:
            css_string += f"""
            .scene{index - 1} {{
            opacity: 0;
            animation: turn-off {duration}s;
              animation-timing-function: ease-in-out;
                animation-delay: {sum_prev}s;
                  animation-fill-mode: both;
                  }}
            """
        sum_prev += screentime
        tag.extend(svg.contents)
        new_children.append(tag)
    css.append(css_string)
    new_children.append(css)
    new_children.reverse()

    header = """<svg fill="none" height="984" viewBox="0 0 1488 984" width="1488" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">"""
    f = open(f"result/final/{i}.svg", "w")
    f.write(header)
    for child in new_children:
        f.write(str(child))
    f.write("</svg>")
    f.close()


