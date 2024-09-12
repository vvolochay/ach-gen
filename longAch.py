from bs4 import BeautifulSoup, NavigableString
from pathlib import Path


images = "_main.svg", "long.svg"
for i in range(46001, 46137):
# for i in range(47001, 47137):
    print(i)
    soups = []
    if not Path(f"46/{i}_main.svg").exists():
        print("skipping")
        continue
    for img in images:
        p = Path(f"46/" + str(i) + img)
        if p.exists():
            with open(p) as f:
                content = f.readlines()
                content = "".join(content)
                soups.append(BeautifulSoup(content, "xml"))
    screentime = 5
    duration = 0.5

    out = soups[0].svg
    new_children = []

    sum_prev = 0
    for index, soup in enumerate(soups):
        svg = soup.svg
        tag = soups[0].new_tag('g', **{'class': f'scene{index}', 'transform': 'translate(0, 932)'})
        sum_prev += screentime
        tag.extend(svg.contents)
        new_children.append(tag)
    new_children.reverse()

    header = """<svg fill="none" height="1080" viewBox="0 0 1920 1080" width="1920" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">"""
    long = """<path d="M0 932H1920V1064C1920 1072.84 1912.84 1080 1904 1080H16C7.16343 1080 0 1072.84 0 1064V932Z" fill="#4AB0D1"/>"""
    f = open(f"res/46/{i}.svg", "w")
    f.write(header)
    f.write(long)
    for child in new_children:
        f.write(str(child))
    f.write("</svg>")
    f.close()


